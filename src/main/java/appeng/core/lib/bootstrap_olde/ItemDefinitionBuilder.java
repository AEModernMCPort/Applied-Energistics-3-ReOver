package appeng.core.lib.bootstrap_olde;

import appeng.api.definitions.IItemDefinition;
import appeng.api.item.IStateItem;
import appeng.core.lib.definitions.ItemDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDefinitionBuilder<I extends Item>
		extends DefinitionBuilder<I, I, IItemDefinition<I>, ItemDefinitionBuilder<I>>
		implements IItemBuilder<I, ItemDefinitionBuilder<I>> {

	@SideOnly(Side.CLIENT)
	private ItemRendering itemRendering;

	//TODO 1.11.2-ReOver - :P
	private CreativeTabs creativeTab = CreativeTabs.REDSTONE;

	ItemDefinitionBuilder(FeatureFactory factory, ResourceLocation registryName, I item){
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

	@Override
	public ItemDefinitionBuilder<I> rendering(ItemRenderingCustomizer callback){
		/*if(Platform.isClient()){
			customizeForClient(callback);
		}*/

		return this;
	}

	@SideOnly(Side.CLIENT)
	private void customizeForClient(ItemRenderingCustomizer callback){
		callback.customize(itemRendering);
	}

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
