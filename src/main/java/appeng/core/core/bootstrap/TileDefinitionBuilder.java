package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.ITileDefinition;
import appeng.core.api.bootstrap.ITileBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.definitions.TileDefinition;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileDefinitionBuilder<T extends TileEntity> extends DefinitionBuilder<Class<T>, Class<T>, ITileDefinition<T>, TileDefinitionBuilder<T>> implements ITileBuilder<T, TileDefinitionBuilder<T>> {

	private Definitions<Block, IBlockDefinition<Block>> blockDefinitions;

	public TileDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, Class<T> tile, Definitions<Block, IBlockDefinition<Block>> blockDefinitions){
		super(factory, registryName, tile, "tile_entity");
		this.blockDefinitions = blockDefinitions;
	}

	@Override
	protected ITileDefinition<T> def(Class<T> t){
		if(t == null){
			return new TileDefinition<T>(registryName, null, null);
		}

		GameRegistry.registerTileEntity(t, registryName.toString());

		return new TileDefinition<T>(registryName, t, (IBlockDefinition) blockDefinitions.get(registryName));
	}

}
