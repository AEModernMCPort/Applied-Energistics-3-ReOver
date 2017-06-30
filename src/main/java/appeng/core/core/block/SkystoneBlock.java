package appeng.core.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

/**
 * @author dpeter99
 */
public class SkystoneBlock extends Block {

	public static final IProperty<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	public SkystoneBlock(){
		super(Material.ROCK);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return super.getStateFromMeta(meta).withProperty(VARIANT, Variant.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(VARIANT).ordinal();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items){
		for(Variant variants : Variant.values()) items.add(new ItemStack(this, 1, variants.ordinal()));
	}

	public enum Variant implements IStringSerializable {
		STONE, BLOCK, BRICK, BRICK_SMALL;

		@Override
		public String getName(){
			return this.name().toLowerCase();
		}
	}

}
