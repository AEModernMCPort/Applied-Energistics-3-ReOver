
package appeng.core.me.bootstrap;


import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.features.AEFeature;
import appeng.core.me.api.part.PartRegistryEntry;
import appeng.core.me.item.ItemPartDefaultTemp;


public class MEFeatureFactory extends FeatureFactory
{

	@Deprecated
	private final Map defaultItemParts = Maps.newHashMap();

	public MEFeatureFactory()
	{
		super();
	}

	public MEFeatureFactory( FeatureFactory parent, AEFeature... defaultFeatures )
	{
		super( parent, defaultFeatures );
	}

	@Deprecated
	public <P extends PartRegistryEntry> PartDefinitionBuilder<P> part( String id, P biome )
	{
		return part( new ResourceLocation( AppEng.MODID, id ), biome );
	}

	public <P extends PartRegistryEntry> PartDefinitionBuilder<P> part( ResourceLocation id, P biome )
	{
		return new PartDefinitionBuilder<P>( this, id, biome ).features( defaultFeatures );
	}

	@Deprecated
	public <P extends PartRegistryEntry> IItemDefinition<ItemPartDefaultTemp<P>> defaultItemPartTemp( P part )
	{
		IItemDefinition<ItemPartDefaultTemp<P>> def = item( part.getRegistryName(), new ItemPartDefaultTemp<>( part ) ).build();
		defaultItemParts.put( part.getRegistryName(), def );
		return def;
	}

	@Override
	public FeatureFactory features( AEFeature... features )
	{
		return new MEFeatureFactory( this, features );
	}

	@Override
	public Map<ResourceLocation, IItemDefinition<Item>> buildDefaultItemBlocks()
	{
		Map result = super.buildDefaultItemBlocks();
		result.putAll( defaultItemParts );
		return result;
	}

}
