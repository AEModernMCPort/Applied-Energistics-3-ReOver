package appeng.core.skyfall.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CertusInfusedBlock extends Block {

	private static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 15);

	public CertusInfusedBlock(){
		super(Material.ROCK);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(VARIANT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(VARIANT, 0);
	}

	@Override
	public int damageDropped(IBlockState state){
		return getMetaFromState(state);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items){
		for(int meta = 0; meta <= 15; meta++) items.add(new ItemStack(this, 1, meta));
	}
}
