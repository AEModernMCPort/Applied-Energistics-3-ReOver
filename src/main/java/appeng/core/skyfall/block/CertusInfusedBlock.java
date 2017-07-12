package appeng.core.skyfall.block;

import appeng.core.skyfall.AppEngSkyfall;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

public class CertusInfusedBlock extends Block {

	private static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 15);

	private static List<IBlockState> getConfig(){
		return AppEngSkyfall.INSTANCE.config.meteorite.getAllowedBlockStates();
	}

	public static boolean isValid(int variant){
		return variant < getConfig().size();
	}

	public static boolean isValid(IBlockState state){
		return getConfig().contains(state);
	}

	public static IBlockState getVariantState(int variant){
		return getConfig().get(variant);
	}

	public static int getStateVariant(IBlockState state){
		return getConfig().indexOf(state);
	}

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
		return getDefaultState().withProperty(VARIANT, meta);
	}

	@Override
	public int damageDropped(IBlockState state){
		return getMetaFromState(state);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items){
		for(int meta = 0; meta <= 15; meta++) if(isValid(meta)) items.add(new ItemStack(this, 1, meta));
	}

	public String getDisplayName(int variant, String def){
		String original;
		if(isValid(variant)){
			IBlockState infusedS = getVariantState(variant);
			Item infusedI = Item.getItemFromBlock(infusedS.getBlock());
			if(infusedI != Items.AIR) original = new ItemStack(infusedI, 1, infusedS.getBlock().damageDropped(infusedS)).getDisplayName();
			else original = infusedS.getBlock().getLocalizedName();
		} else {
			original = "tile.null.name";
		}
		return String.format(def, original);
	}

}
