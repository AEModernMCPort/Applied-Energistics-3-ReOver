package appeng.core.lib.bootstrap_olde;

import appeng.api.bootstrap.DefinitionBuilder;
import appeng.api.definitions.IMaterialDefinition;
import appeng.core.api.material.Material;

public interface IMaterialBuilder<M extends Material, MM extends IMaterialBuilder<M, MM>>
		extends DefinitionBuilder<M, IMaterialDefinition<M>, MM> {

}
