
package appeng.api.item;


import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import appeng.api.item.IStateItem.State.Property;


public interface IStateItem<I extends Item & IStateItem<I>>
{

	boolean isValid( Property property );

	<V> Property<V> getProperty( String name );

	State<I> getState( ItemStack itemstack );

	ItemStack getItemStack( State<I> state, int amount );

	State<I> getDefaultState();

	public class State<I extends Item & IStateItem<I>>
	{

		private final I item;
		private final ImmutableMap<Property, ?> properties;

		public State( I item, Map<Property, ?> properties )
		{
			this.item = item;
			this.properties = ImmutableMap.copyOf( properties );
		}

		public State( I item )
		{
			this.item = item;
			this.properties = ImmutableMap.of();
		}

		public I getItem()
		{
			return item;
		}

		public ItemStack toItemStack( int amount )
		{
			return item.getItemStack( this, amount );
		}

		public Map<Property, ?> getProperties()
		{
			return properties;
		}

		public <V> V getValue( Property<V> property )
		{
			return (V) properties.get( property );
		}

		public <V> State withProperty( Property<V> property, V value )
		{
			assert item.isValid( property ) && property.isValid( value );
			Map map = new HashMap<>();
			map.putAll( properties );
			map.put( property, value );
			return (State) new State( item, map );
		}

		public interface Property<V>
		{

			String getName();

			boolean isValid( V value );

		}

	}

}
