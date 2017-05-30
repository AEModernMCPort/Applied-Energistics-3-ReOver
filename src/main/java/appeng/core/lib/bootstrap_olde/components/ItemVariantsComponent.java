package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.InitializationComponent;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

public class ItemVariantsComponent implements InitializationComponent {

	private final Item item;

	private final Collection<ResourceLocation> resources;

	public ItemVariantsComponent(Item item, Collection<ResourceLocation> resources){
		this.item = item;
		this.resources = resources;
	}

	@Override
	public void preInit(){
		ResourceLocation[] resourceArr = resources.toArray(new ResourceLocation[0]);
		ModelBakery.registerItemVariants(item, resourceArr);
	}
}
