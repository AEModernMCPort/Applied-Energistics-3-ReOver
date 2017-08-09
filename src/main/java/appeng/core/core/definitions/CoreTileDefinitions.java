package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.api.definition.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import appeng.core.core.api.definitions.ICoreTileDefinitions;
import appeng.core.core.tile.IonEnvironmentTile;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class CoreTileDefinitions extends Definitions<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> implements ICoreTileDefinitions {

	public CoreTileDefinitions(DefinitionFactory registry){
		registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "ion_environment"), ih(IonEnvironmentTile.class)).build();
	}

	private DefinitionFactory.InputHandler<TileRegistryEntry<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<TileRegistryEntry<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
