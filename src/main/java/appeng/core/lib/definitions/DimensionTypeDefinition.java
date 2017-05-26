
package appeng.core.lib.definitions;


import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import appeng.api.definitions.IDimensionTypeDefinition;


public class DimensionTypeDefinition<D extends DimensionType> extends Definition<D> implements IDimensionTypeDefinition<D>
{

	public DimensionTypeDefinition( ResourceLocation identifier, D dimensionType )
	{
		super( identifier, dimensionType );
	}

	@Override
	public boolean isSameAs( Object other )
	{
		// TODO 1.11.2-CD:A - Add checks
		if( super.isSameAs( other ) )
		{
			return true;
		}
		else
		{
			if( isEnabled() )
			{
				D dimensionType = maybe().get();
				if( other instanceof World )
				{
					return ( (World) other ).provider.getDimensionType() == dimensionType;
				}
			}
			return false;
		}
	}

}
