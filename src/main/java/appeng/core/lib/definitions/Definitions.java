
package appeng.core.lib.definitions;


import java.lang.reflect.Field;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;


public class Definitions<T, D extends IDefinition<T>> implements IDefinitions<T, D>
{

	private ImmutableMap<ResourceLocation, D> map;

	/**
	 * Make sure to call in the end of the constructor.
	 */
	protected final void init()
	{
		init( null );
	}

	protected final void init( Map<ResourceLocation, D> extraEntries )
	{
		assert map == null;
		ImmutableMap.Builder builder = ImmutableMap.builder();
		for( Field field : this.getClass().getDeclaredFields() )
		{
			if( field.getType().isAssignableFrom( IDefinition.class ) )
			{
				try
				{
					field.setAccessible( true );
					IDefinition<T> def = (IDefinition<T>) field.get( this );
					builder.put( def.identifier(), def );
				}
				catch( ReflectiveOperationException e )
				{
					// ;(
				}
			}
		}
		if( extraEntries != null )
		{
			builder.putAll( extraEntries );
		}
		map = builder.build();
	}

	@Override
	public D get( ResourceLocation identifier )
	{
		return map.get( identifier );
	}

}
