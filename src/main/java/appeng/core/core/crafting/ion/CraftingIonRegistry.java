package appeng.core.core.crafting.ion;

import appeng.api.bootstrap.InitializationComponent;
import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.*;
import appeng.core.core.api.crafting.ion.IonEnvironment;
import appeng.core.core.api.definitions.ICoreIonDefinitions;
import appeng.core.core.crafting.ion.temp.InWorldIonEnvTemperatureListener;
import appeng.core.lib.capability.DelegateCapabilityStorage;
import appeng.core.lib.capability.SingleCapabilityProvider;
import code.elix_x.excomms.color.RGBA;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.reflect.InheritanceUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CraftingIonRegistry implements InitializationComponent.PreInit {

	@CapabilityInject(IonEnvironment.class)
	public static Capability<IonEnvironment> ionEnvironmentCapability;

	@CapabilityInject(IonProvider.class)
	public static Capability<IonProvider> ionProviderCapability;

	@CapabilityInject(InWorldIonEnvTemperatureListener.class)
	public static Capability<InWorldIonEnvTemperatureListener> ionEnvTemperatureListenerCapability;

	@Override
	public void preInit(){
		CapabilityManager.INSTANCE.register(IonEnvironment.class, new DelegateCapabilityStorage<>(), appeng.core.core.crafting.ion.IonEnvironment::new);

		CapabilityManager.INSTANCE.register(IonProvider.class, new Capability.IStorage<IonProvider>() {

			@Nullable
			@Override
			public NBTBase writeNBT(Capability<IonProvider> capability, IonProvider instance, EnumFacing side){
				return null;
			}

			@Override
			public void readNBT(Capability<IonProvider> capability, IonProvider instance, EnumFacing side, NBTBase nbt){

			}

		}, IonProviderImpl::new);

		CapabilityManager.INSTANCE.register(InWorldIonEnvTemperatureListener.class, new DelegateCapabilityStorage<>(), InWorldIonEnvTemperatureListener::new);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private ICoreIonDefinitions ionDefinitions(){
		return AppEngCore.INSTANCE.definitions(Ion.class);
	}

	@SubscribeEvent
	public void attachProviderByOreDict(AttachCapabilitiesEvent<ItemStack> event){
		if(!event.getObject().isEmpty()){
			List<Pair<Ion, Integer>> ions = Arrays.stream(OreDictionary.getOreIDs(event.getObject())).mapToObj(OreDictionary::getOreName).map(AppEngCore.INSTANCE.config.ionCraftingConfig.oreDict2IonsC::get).flatMap(Collection::stream).collect(Collectors.toList());
			if(!ions.isEmpty()) event.addCapability(new ResourceLocation(AppEng.MODID, "ion_provider"), new SingleCapabilityProvider<>(ionProviderCapability, new IonProviderImpl(ions)));
		}
	}

	public BiMap<Fluid, Fluid> normal2ionized = HashBiMap.create();
	public BiMap<Fluid, Fluid> ionized2normal = normal2ionized.inverse();

	public void registerEnvironmentFluid(@Nonnull Fluid fluid){
		normal2ionized.put(fluid, fluid);
	}

	public void registerIonVariant(Fluid original, Fluid ionized){
		normal2ionized.put(original, ionized);
	}

	public void onIonEntityItemTick(EntityItem item, IonProvider ionProvider){
		//TODO Expand to bounding box collision logic
		World world = item.world;
		BlockPos pos = new BlockPos(item.posX, item.posY, item.posZ);
		IBlockState block = world.getBlockState(pos);
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block.getBlock());
		if(normal2ionized.containsKey(fluid) && ionProvider.isReactive(fluid)){
			Fluid ionized = normal2ionized.get(fluid);
			world.setBlockToAir(pos);
			FluidUtil.tryPlaceFluid(null, world, pos, new FluidTank(ionized, Fluid.BUCKET_VOLUME, Fluid.BUCKET_VOLUME), new FluidStack(ionized, Fluid.BUCKET_VOLUME));
			enterIonEnv(world, pos, item, ionProvider);
		}
	}

	public void onIonEntityItemEnterEnvironment(World world, BlockPos pos, EntityItem item, IonProvider ionProvider){
		IBlockState block = world.getBlockState(pos);
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block.getBlock());
		if(ionized2normal.containsKey(fluid)) enterIonEnv(world, pos, item, ionProvider);
	}

	protected void enterIonEnv(World world, BlockPos pos, EntityItem item, IonProvider ionProvider){
		IonEnvironment environment = world.getTileEntity(pos).getCapability(ionEnvironmentCapability, null);
		for(int i = 0; i < item.getItem().getCount(); i++) ionProvider.getIons(world.rand).forEach(environment::addIons);
		world.markBlockRangeForRenderUpdate(pos, pos);
		item.setDead();
	}

	public RGBA getColor(IonEnvironment environment, RGBA original){
		/*MutableObject<RGBA> color = new MutableObject<>(original);
		environment.getIons().forEach(ion -> color.setValue(blend(color.getValue(), ion.getColorModifier(), amount2mul(environment.getAmount(ion)))));
		return color.getValue();*/
		Set<RGBA> colors = new HashSet<>();
		float aSum = 0.1f + (float) environment.getIons().values().stream().mapToDouble(this::amount2mul).sum();
		colors.add(new RGBA(original.getRF(), original.getGF(), original.getBF(), 0.1f * original.getAF() / aSum));
		environment.getIons().forEach((ion, amount) -> colors.add(new RGBA(ion.getColorModifier().getRF(), ion.getColorModifier().getGF(), ion.getColorModifier().getBF(), amount2mul(amount) / aSum)));
		return blend(colors);
	}

	public RGBA blend(Set<RGBA> colors){
		double r = colors.stream().mapToDouble(color -> color.getRF() * color.getAF()).sum();
		double g = colors.stream().mapToDouble(color -> color.getGF() * color.getAF()).sum();
		double b = colors.stream().mapToDouble(color -> color.getBF() * color.getAF()).sum();
		return new RGBA((float) r, (float) g, (float) b, 1f);
	}

	public float amount2mul(int amount){
		return -1f/(2*amount) + 1f;
	}

	public Map<IonEnvironmentContext.Change, Map<Class, IonEnvironmentProductConsumer>> consumers = new HashMap<>();

	protected Map<Class, IonEnvironmentProductConsumer> getChangeMap(IonEnvironmentContext.Change change){
		Map<Class, IonEnvironmentProductConsumer> map = consumers.get(change);
		if(map == null) consumers.put(change, map = new HashMap<>());
		return map;
	}

	public <T> void registerProductConsumer(Class<T> type, IonEnvironmentProductConsumer<T> consumer, IonEnvironmentContext.Change... changes){
		for(IonEnvironmentContext.Change change : changes) getChangeMap(change).put(type, consumer);
	}

	public List<Pair<Class, Consumer>> compileProductConsumersL(IonEnvironmentContext context, IonEnvironmentContext.Change change){
		return getChangeMap(change).entrySet().stream().map(entry -> new ImmutablePair<>(entry.getKey(), entry.getValue().createConsumer(context))).collect(Collectors.toList());
	}

	public Function<Class, Optional<Consumer>> compileProductConsumersF(IonEnvironmentContext context, IonEnvironmentContext.Change change){
		List<Pair<Class, Consumer>> consumers = compileProductConsumersL(context, change);
		return clas -> consumers.stream().filter(consumer -> consumer.getLeft().isAssignableFrom(clas)).sorted(Comparator.comparingInt(consumer -> InheritanceUtils.distance(clas, consumer.getLeft()))).findFirst().map(Pair::getRight);
	}

	public Consumer compileProductConsumersC(IonEnvironmentContext context, IonEnvironmentContext.Change change){
		Function<Class, Optional<Consumer>> consumers = compileProductConsumersF(context, change);
		return product -> consumers.apply(product.getClass()).ifPresent(consumer -> consumer.accept(product));
	}

}
