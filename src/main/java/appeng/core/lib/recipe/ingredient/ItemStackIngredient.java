package appeng.core.lib.recipe.ingredient;

import appeng.api.recipe.IGIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

@FunctionalInterface
public interface ItemStackIngredient extends IGIngredient<ItemStack> {

	@Override
	default boolean test(ItemStack itemstack){
		return itemstack.isEmpty() ? acceptsEmpty() : testItem(itemstack.getItem()) && testMeta(itemstack.getItemDamage());
	}

	default boolean acceptsEmpty(){
		return false;
	}

	boolean testItem(Item item);

	default boolean testMeta(int meta){
		return true;
	}

	class SimplyItem implements ItemStackIngredient {

		private final Item item;

		public SimplyItem(Item item){
			this.item = item;
		}

		@Override
		public boolean testItem(Item item){
			return item == this.item;
		}

	}

	class ItemMeta extends SimplyItem {

		private final int meta;

		public ItemMeta(Item item, int meta){
			super(item);
			this.meta = meta;
		}

		@Override
		public boolean testMeta(int meta){
			return meta == this.meta;
		}

	}

	class EquivalentStack implements IGIngredient<ItemStack> {

		private final ItemStack itemstack;

		public EquivalentStack(ItemStack itemstack){
			this.itemstack = itemstack;
		}

		@Override
		public boolean test(ItemStack itemstack){
			return ItemStack.areItemStacksEqual(itemstack, this.itemstack);
		}

	}

	class OreDictionaryEntry implements IGIngredient<ItemStack> {

		private final String oreDict;
		private final boolean strict;

		public OreDictionaryEntry(String oreDict, boolean strict){
			this.oreDict = oreDict;
			this.strict = strict;
		}

		public OreDictionaryEntry(String oreDict){
			this(oreDict, false);
		}

		@Override
		public boolean test(ItemStack itemstack){
			return OreDictionary.containsMatch(strict, OreDictionary.getOres(oreDict), itemstack);
		}
	}

}
