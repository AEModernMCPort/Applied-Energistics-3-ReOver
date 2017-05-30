package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IItemDefinition;
import appeng.api.item.IStateItem;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.bootstrap_olde.ItemRenderingCustomizer;
import appeng.core.lib.bootstrap_olde.ItemSubDefinition;
import appeng.core.lib.definitions.ItemDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDefinitionBuilder<I extends Item> extends DefinitionBuilder<I, I, IItemDefinition<I>, ItemDefinitionBuilder<I>> implements IItemBuilder<I, ItemDefinitionBuilder<I>> {

	//TODO 1.11.2-ReOver - Be back?
	/*@SideOnly(Side.CLIENT)
	private ItemRendering itemRendering;*/

	//TODO 1.11.2-ReOver - :P
	private CreativeTabs creativeTab = CreativeTabs.REDSTONE;

	ItemDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, I item){
		super(factory, registryName, item);
		/*if(Platform.isClient()){
			itemRendering = new ItemRendering();
		}*/
	}

	@Override
	public ItemDefinitionBuilder<I> creativeTab(CreativeTabs tab){
		this.creativeTab = tab;
		return this;
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

		if(item instanceof IStateItem){
			preInit(def -> definition.setSubDefinition(() -> new ItemSubDefinition(((IStateItem) item).getDefaultState(), definition)));
		}

		return definition;
	}
}
