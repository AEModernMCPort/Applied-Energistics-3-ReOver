package appeng.core.skyfall.block;

import appeng.core.AppEng;
import appeng.core.lib.util.BlockState2String;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Optional;

public class CertusInfusedBlock extends Block {

	public static ResourceLocation formatToInfused(ResourceLocation original){
		return new ResourceLocation(AppEng.MODID, String.format("certus_infused_%s_%s", original.getResourceDomain(), original.getResourcePath()));
	}

	//WE DO NOT SUPPORT STATES NOR META C:P
	public static Optional<CertusInfusedBlock> getInfused(Block original){
		return Optional.ofNullable(BlockState2String.getBlockOrNull(formatToInfused(original.getRegistryName()))).map(block -> block instanceof CertusInfusedBlock ? (CertusInfusedBlock) block : null);
	}

	protected ResourceLocation original;

	public CertusInfusedBlock(ResourceLocation original){
		super(Material.ROCK);
		setCreativeTab(CreativeTabs.DECORATIONS);
		this.original = original;
	}

	public IBlockState getOriginal(){
		return BlockState2String.fromString(original.toString());
	}

	public String getDisplayName(String def){
		String original;
		IBlockState infusedS = getOriginal();
		Item infusedI = Item.getItemFromBlock(infusedS.getBlock());
		if(infusedI != Items.AIR) original = new ItemStack(infusedI, 1, infusedS.getBlock().damageDropped(infusedS)).getDisplayName();
		else original = infusedS.getBlock().getLocalizedName();
		return String.format(def, original);
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

}
