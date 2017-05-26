
package appeng.core.lib.bootstrap;


import appeng.api.definitions.IMaterialDefinition;
import appeng.core.api.material.Material;


public interface IMaterialBuilder<M extends Material, MM extends IMaterialBuilder<M, MM>> extends IDefinitionBuilder<M, IMaterialDefinition<M>, MM>
{

}
