package appeng.core.me.parts.part;

import appeng.api.bootstrap.InitializationComponent;
import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartEvent;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.part.PartGroup;
import appeng.core.me.parts.container.PartAccessUtil;
import appeng.core.me.parts.container.PartInfoImpl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Stream;

import static appeng.core.me.parts.part.PartsHelper.logger;

public class PartGroupsHelper<IntP extends Part<IntP, IntPS>, IntPS extends Part.State<IntP, IntPS>, IntPG extends PartGroup<IntPG, IntPGS>, IntPGS extends Part.State<IntPG, IntPGS>> implements InitializationComponent {

	PartsHelper partsHelper;

	Map<IntPG, Group<IntPG, IntPGS>> compiledGroups = new HashMap<>();
	Multimap<Pair<IntP, PartRotation>, InGroup<IntP, IntPS>> inGroups = ArrayListMultimap.create();

	private boolean compiled = false;

	public PartGroupsHelper(PartsHelper partsHelper){
		this.partsHelper = partsHelper;
	}

	@Override
	public void preInit(){
		MinecraftForge.EVENT_BUS.register(this);
	}

	void loadGroups(){
		logger.info("Compiling groups");
		long time = System.currentTimeMillis();
		compiled = false;

		AppEngME.INSTANCE.getPartRegistry().forEach(part -> {
			if(part instanceof PartGroup) compiledGroups.put((IntPG) part, new Group<>((IntPG) part));
		});
		compiledGroups.values().stream().flatMap(Group::getAllVariants).forEach(Group.GroupVariant::compile);

		Multimap<Pair<IntP, PartRotation>, InGroup<IntP, IntPS>> inGroupsUnsorted = HashMultimap.create();
		compiledGroups.values().stream().flatMap(Group::getAllVariants).forEach(variant -> variant.parts.forEach(part -> inGroupsUnsorted.put(new ImmutablePair<>(part.getPart(), part.getPositionRotation().getRotation()), new InGroup<>(part, variant))));
		inGroupsUnsorted.keySet().forEach(part -> inGroupsUnsorted.get(part).stream().sorted(Comparator.comparing(group -> group.group.parts.size())).forEach(group -> this.inGroups.put(part, group)));

		compiled = true;
		logger.info("Compiled groups in " + (System.currentTimeMillis() - time));
	}

	public Optional<InGroup<IntP, IntPS>> canFormGroup(PartsAccess access, PartInfo<IntP, IntPS> caller){
		for(InGroup<IntP, IntPS> group : inGroups.get(new ImmutablePair<>(caller.getPart(), caller.getPositionRotation().getRotation()))){
			if(group.canForm(access, caller)) return Optional.of(group);
		}
		return Optional.empty();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPartPlacePost(PartEvent.Set<IntP, IntPS> event){
		if(compiled){
			Optional<InGroup<IntP, IntPS>> group = canFormGroup(event.getPartsAccess(), new PartInfoImpl<>(event.getPart(), event.getPositionRotation(), null));
			group.ifPresent(g -> g.form(event.getPartsAccess(), new PartInfoImpl<>(event.getPart(), event.getPositionRotation(), null)));
		}
	}

	class Group<P extends PartGroup<P, S>, S extends Part.State<P, S>> {

		final P part;
		final Map<PartRotation, GroupVariant> variants = new HashMap<>();

		Group(P part){
			this.part = part;
			part.possibleRotations().forEach(rot -> variants.put(rot, new GroupVariant(rot, part.compileRequiredParts(rot))));
		}

		Stream<GroupVariant> getAllVariants(){
			return variants.values().stream();
		}

		class GroupVariant {

			final PartRotation rot;
			final PartsAccess access;
			List<PartInfo<IntP, IntPS>> parts = new ArrayList<>();

			GroupVariant(PartRotation rot, PartsAccess access){
				this.rot = rot;
				this.access = access;
			}

			void compile(){
				Set<PartInfo<IntP, IntPS>> parts = new HashSet<>();
				partsHelper.getVoxels(part, new PartPositionRotation(new VoxelPosition(), rot)).forEach(voxel -> access.<IntP, IntPS>getPart(voxel).ifPresent(parts::add));

				Map<IntP, Integer> count = new HashMap<>();
				parts.forEach(info -> {
					Integer c = count.get(info.getPart());
					if(c == null) c = 0;
					count.put(info.getPart(), c + 1);
				});
				parts.stream().sorted(Comparator.comparing(info -> count.get(info.getPart()))).forEach(this.parts::add);
			}

			Stream<VoxelPosition> getVoxels(){
				return partsHelper.getVoxels(part, new PartPositionRotation(new VoxelPosition(), rot));
			}

			boolean canForm(PartsAccess access, VoxelPosition position){
				return PartAccessUtil.regionsMatchOccupiedVoxels(this.access, access, new VoxelPosition(), position, getVoxels());
//				return parts.stream().allMatch(part -> partsHelper.getVoxels(part.getPart(), part.getPositionRotation().addPos(position)).map(access::getPart).distinct().allMatch(lpart -> lpart.isPresent() && lpart.get().getPart().equals(part.getPart())));
			}

			void form(PartsAccess.Mutable access, VoxelPosition position){
				getVoxels().map(position::add).forEach(access::removePart);
				access.setPart(new PartPositionRotation(position, rot), part.createNewState());
			}

		}

	}

	class InGroup<P extends Part<P, S>, S extends Part.State<P, S>> {

		final P part;
		final PartPositionRotation positionRotation;
		final Group<IntPG, IntPGS>.GroupVariant group;

		InGroup(PartInfo<P, S> info, Group<IntPG, IntPGS>.GroupVariant variant){
			this.part = info.getPart();
			this.positionRotation = info.getPositionRotation();
			this.group = variant;
		}

		boolean canForm(PartsAccess access, PartInfo<P, S> caller){
			return group.canForm(access, caller.getPositionRotation().posAsRelativeTo(positionRotation).getPosition());
		}

		void form(PartsAccess.Mutable access, PartInfo<P, S> caller){
			group.form(access, caller.getPositionRotation().posAsRelativeTo(positionRotation).getPosition());
		}

	}

}
