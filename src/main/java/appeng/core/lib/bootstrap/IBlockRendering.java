package appeng.core.lib.bootstrap;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiFunction;

/**
 * Allows for client-side rendering to be customized in the context of block/item registration.
 */
public interface IBlockRendering {

	@SideOnly(Side.CLIENT)
	IBlockRendering modelCustomizer(BiFunction<ModelResourceLocation, IBakedModel, IBakedModel> customizer);

	@SideOnly(Side.CLIENT)
	IBlockRendering blockColor(IBlockColor blockColor);

	@SideOnly(Side.CLIENT)
	IBlockRendering stateMapper(IStateMapper mapper);

	@SideOnly(Side.CLIENT)
	IBlockRendering tesr(TileEntitySpecialRenderer<?> tesr);

}
