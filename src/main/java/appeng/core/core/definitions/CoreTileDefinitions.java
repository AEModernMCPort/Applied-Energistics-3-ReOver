package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.ITileDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreTileDefinitions;
import appeng.core.core.client.bootstrap.TesrComponent;
import appeng.core.item.DummyTile;
import appeng.core.item.DummyTileRenderer;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public class CoreTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements ICoreTileDefinitions {

	public CoreTileDefinitions(DefinitionFactory registry){
		IDefinitionBuilder builder = registry.definitionBuilder(new ResourceLocation(AppEng.MODID,"component_test_tile"), ih(DummyTile.class));
		builder.initializationComponent(Side.CLIENT, new TesrComponent(() -> Optional.of(new DummyTileRenderer())));
		//builder.build();
}

	private DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
