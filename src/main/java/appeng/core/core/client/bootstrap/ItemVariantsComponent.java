package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definition.IDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

/**
 * @author Fredi100
 */
public class ItemVariantsComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IDefinition<Item>> {

	private final Collection<ResourceLocation> resources;

	public ItemVariantsComponent(Collection<ResourceLocation> resources){
		this.resources = resources;
	}

	@Override
	public void preInit(IDefinition<Item> def){
		ResourceLocation[] resourceArr = resources.toArray(new ResourceLocation[0]);
		ModelBakery.registerItemVariants(def.maybe().get(), resourceArr);
	}
}