
package appeng.core.definitions;


import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreItemDefinitions;
import appeng.core.client.render.ItemCrystalSeedRendering;
import appeng.core.client.render.item.ItemMaterialRendering;
import appeng.core.item.ItemCrystalSeed;
import appeng.core.item.ItemMaterial;
import appeng.core.item.ToolQuartzCuttingKnife;
import appeng.core.item.ToolQuartzWrench;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.tools.AppEngTools;


public class CoreItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements ICoreItemDefinitions
{
	
	private final IItemDefinition material;

	public CoreItemDefinitions( FeatureFactory registry )
	{
		this.material = registry.item( new ResourceLocation( AppEng.MODID, "material" ), new ItemMaterial() ).build();
		
		init( registry.buildDefaultItemBlocks() );
	}

}
