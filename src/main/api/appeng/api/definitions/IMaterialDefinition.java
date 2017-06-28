package appeng.api.definitions;

import appeng.api.definitions.sub.IItemSubDefinition;
import appeng.api.item.IStateItemState;
import appeng.core.api.item.IItemMaterial;
import appeng.core.api.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public interface IMaterialDefinition<M extends Material> extends IDefinition<M> {

	<S extends IStateItemState<I>, I extends Item & IItemMaterial<I>, D extends IItemSubDefinition<S, I>> Optional<D> maybeAsSubDefinition();

	default Optional<ItemStack> maybeStack(int amount){
		return maybeAsSubDefinition().map(subDefinition -> subDefinition.maybe().get().toItemStack(amount));
	}

}
