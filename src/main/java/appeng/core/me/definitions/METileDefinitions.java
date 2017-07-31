package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.ITileBuilder;
import appeng.core.core.api.definition.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMETileDefinitions;
import appeng.core.me.tile.PartsContainerTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class METileDefinitions extends Definitions<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> implements IMETileDefinitions {

	private final ITileDefinition partsContainer;

	public METileDefinitions(DefinitionFactory factory){
		partsContainer = factory.<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>, ITileBuilder<TileEntity, ?>, Class>definitionBuilder(new ResourceLocation(AppEng.MODID, "parts_container"), ih(PartsContainerTile.class)).setFeature(null).build();
	}

	private DefinitionFactory.InputHandler<? super TileRegistryEntry<TileEntity>, Class> ih(Class tile){
		return new DefinitionFactory.InputHandler<TileRegistryEntry<TileEntity>, Class>(tile) {};
	}

}
