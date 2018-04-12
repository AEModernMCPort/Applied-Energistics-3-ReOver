package appeng.core.me.api.network.device;

import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.behavior.BehaviorDriven;
import appeng.core.me.api.parts.container.PartsAccess;
import code.elix_x.excomms.optional.NullableOptional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface ExportBus {

	interface Network<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends NetDevice<N, S>, BehaviorDriven<Behavior> {

		long ticksSinceLastUpdate();

		//TODO Generalize
		boolean canAcceptForExport();

		//TODO Generalize
		boolean addForExport(ItemStack stack);

		//TODO Generalize
		Optional<ItemStack> nextItemForExport();

		//TODO Generalize
		boolean confirmExport(ItemStack stack);

		//TODO Generalize
		boolean export(ItemStack stack);

	}

	interface Part<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends appeng.core.me.api.parts.part.Part<P, S> {

	}

	interface Physical<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends PhysicalDevice<N, S>, Part.State<P, S> {

	}

	interface Behavior {

		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult update(){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult updatePhysical(){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult onRightClick(@Nonnull PartsAccess.Mutable world, @Nonnull World theWorld, @Nonnull EntityPlayer player, @Nonnull EnumHand hand){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult canAcceptForExport(){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult addForExport(ItemStack stack){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> NullableOptional<ItemStack> nextItemForExport(){
			return NullableOptional.empty();
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult confirmExport(ItemStack stack){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult exportItem(@Nonnull ItemStack stack){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

	}

}
