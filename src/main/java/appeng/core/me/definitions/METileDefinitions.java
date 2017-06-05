package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMETileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class METileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements IMETileDefinitions {

	public METileDefinitions(DefinitionFactory registry){

	}

}
