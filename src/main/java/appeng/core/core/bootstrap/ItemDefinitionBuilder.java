package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import appeng.api.item.IStateItem;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.definitions.ItemDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;

public class ItemDefinitionBuilder<I extends Item> extends DefinitionBuilder<I, I, IItemDefinition<I>, ItemDefinitionBuilder<I>> implements IItemBuilder<I, ItemDefinitionBuilder<I>> {

	//TODO 1.11.2-ReOver - Be back?
	/*@SideOnly(Side.CLIENT)
	private ItemRendering itemRendering;*/

	//TODO 1.11.2-ReOver - :P
	private CreativeTabs creativeTab = CreativeTabs.REDSTONE;

	public ItemDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, I item){
		super(factory, registryName, item, "item");
		/*if(Platform.isClient()){
			itemRendering = new ItemRendering();
		}*/
	}

	@Override
	public ItemDefinitionBuilder<I> creativeTab(CreativeTabs tab){
		this.creativeTab = tab;
		return this;
	}

	@Override
	public ItemDefinitionBuilder<I> defaultModel(String variant){
		return this.<IDefinitionBuilder.DefinitionInitializationComponent.PreInit<I, IItemDefinition<I>>>initializationComponent(Side.CLIENT, def -> ModelLoader.setCustomModelResourceLocation(def.maybe().get(), 0, new ModelResourceLocation(def.identifier(), variant != null ? variant : "inventory")));
	}

	/*@SideOnly(Side.CLIENT)
	private void customizeForClient(ItemRenderingCustomizer callback){
		callback.customize(itemRendering);
	}*/

	@Override
	public IItemDefinition<I> def(I item){
		item.setUnlocalizedName(registryName.getResourceDomain() + "." + registryName.getResourcePath());
		item.setCreativeTab(creativeTab);

		/*if(Platform.isClient()){
			itemRendering.apply(factory, item);
		}*/

		ItemDefinition definition = new ItemDefinition(registryName, item);

		if(item instanceof IStateItem)
			definition.setSubDefinition(() -> new ItemSubDefinition(((IStateItem) item).getDefaultState(), definition));

		return definition;
	}
}
