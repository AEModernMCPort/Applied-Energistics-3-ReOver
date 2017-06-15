package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class TesrComponent implements IDefinitionBuilder.DefinitionInitializationComponent<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> {

	private final Supplier<Optional<TileEntitySpecialRenderer<? super TileEntity>>> tesr;

	public TesrComponent(Supplier<Optional<TileEntitySpecialRenderer<? super TileEntity>>> tesr){
		this.tesr = tesr;
	}

	@Override
	public void preInit(ITileDefinition<TileEntity> def){
		tesr.get().ifPresent(tileEntitySpecialRenderer -> ClientRegistry.bindTileEntitySpecialRenderer(def.maybe().get().getTileClass(), tesr.get().get()));
	}
}
