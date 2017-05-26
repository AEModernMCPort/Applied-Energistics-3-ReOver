
package appeng.core.me.bootstrap;


import appeng.core.lib.bootstrap.IDefinitionBuilder;
import appeng.core.me.api.definitions.IPartDefinition;
import appeng.core.me.api.part.PartRegistryEntry;


public interface IPartBuilder<P extends PartRegistryEntry, PP extends IPartBuilder<P, PP>> extends IDefinitionBuilder<P, IPartDefinition<P>, PP>
{

}
