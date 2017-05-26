package appeng.core.lib.bootstrap.components;

import appeng.core.lib.bootstrap.IBootstrapComponent;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Collection;

public class ItemVariantsComponent implements IBootstrapComponent {

	private final Item item;

	private final Collection<ResourceLocation> resources;

	public ItemVariantsComponent(Item item, Collection<ResourceLocation> resources){
		this.item = item;
		this.resources = resources;
	}

	@Override
	public void preInit(Side side){
		ResourceLocation[] resourceArr = resources.toArray(new ResourceLocation[0]);
		ModelBakery.registerItemVariants(item, resourceArr);
	}
}
