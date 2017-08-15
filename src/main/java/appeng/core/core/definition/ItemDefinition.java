package appeng.core.core.definition;

import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.lib.definition.Definition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class ItemDefinition<I extends Item> extends Definition<I> implements IItemDefinition<I> {

	public ItemDefinition(ResourceLocation identifier, I item){
		super(identifier, item);
	}

	@Override
	public Optional<ItemStack> maybeStack(int stackSize){
		return isEnabled() ? Optional.of(new ItemStack(maybe().get(), stackSize)) : Optional.empty();
	}

	@Override
	public boolean isSameAs(Object other){
		if(super.isSameAs(other)){
			return true;
		} else {
			if(isEnabled()){
				Item item = maybe().get();
				if(other instanceof ItemStack){
					return ((ItemStack) other).getItem() == item;
				}
			}
			return false;
		}
	}

}
