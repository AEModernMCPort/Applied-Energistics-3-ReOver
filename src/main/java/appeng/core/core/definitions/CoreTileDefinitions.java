package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.ITileDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.ITileBuilder;
import appeng.core.api.definitions.ICoreTileDefinitions;
import appeng.core.core.bootstrap.TesrComponent;
import appeng.core.item.DummyTile;
import appeng.core.item.DummyTileRenderer;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

public class CoreTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements ICoreTileDefinitions {

	public CoreTileDefinitions(DefinitionFactory registry){
		//<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>
		registry.<TileEntity, ITileDefinition<TileEntity>, ITileBuilder<TileEntity, ?>, TileEntity>definitionBuilder(new ResourceLocation(AppEng.MODID,"component_test_tile"), ih(DummyTile.class))
		//<IDefinitionBuilder.DefinitionInitializationComponent.Init<Item, IItemDefinition<Item>>>
		.initializationComponent(Side.CLIENT, new TesrComponent(() -> new DummyTileRenderer()))
		.build();
}

	private DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
