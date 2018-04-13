package appeng.core.me.api.network.device;

import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.behavior.BehaviorDriven;
import code.elix_x.excomms.optional.NullableOptional;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface ImportBus {

	interface Network<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends NetDevice<N, S>, BehaviorDriven<Behavior> {

		long ticksSinceLastUpdate();

		//TODO Generalize
		boolean canAcceptForImport();

		//TODO Generalize
		boolean addForImport(ItemStack stack);

		//TODO Generalize
		Optional<ItemStack> nextItemForImport();

		//TODO Generalize
		boolean confirmImport(ItemStack stack);

		//TODO Generalize
		boolean importItem(ItemStack stack);

	}

	interface Part<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends appeng.core.me.api.parts.part.Part<P, S> {

	}

	interface Physical<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends PhysicalDevice<N, S>, ExportBus.Part.State<P, S> {

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

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult canAcceptForImport(){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult addForImport(ItemStack stack){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> NullableOptional<ItemStack> nextItemForImport(){
			return NullableOptional.empty();
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult confirmImport(ItemStack stack){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		//TODO Generalize
		@Nonnull
		default <P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> BehaviorDriven.BehaviorOperationResult importItem(@Nonnull ItemStack stack){
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

	}

}
