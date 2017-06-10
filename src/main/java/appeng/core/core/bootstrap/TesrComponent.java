package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.ITileDefinition;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * @author Fredi100
 */
public class TesrComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Class<TileEntity>, ITileDefinition<TileEntity>> {

	private final TileEntitySpecialRenderer<? super TileEntity> tesr;

	public TesrComponent(TileEntitySpecialRenderer<? super TileEntity> tesr){
		this.tesr = tesr;
	}

	@Override
	public void preInit(ITileDefinition<TileEntity> def){
		ClientRegistry.bindTileEntitySpecialRenderer(def.maybe().get(), tesr);
	}
}
