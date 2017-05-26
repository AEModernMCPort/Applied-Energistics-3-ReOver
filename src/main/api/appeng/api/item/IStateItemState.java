package appeng.api.item;

import com.google.common.collect.ImmutableMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class IStateItemState<I extends Item & IStateItem<I>> {

	private final I item;
	private final ImmutableMap<Property, ?> properties;

	public IStateItemState(I item, Map<Property, ?> properties){
		this.item = item;
		this.properties = ImmutableMap.copyOf(properties);
	}

	public IStateItemState(I item){
		this.item = item;
		this.properties = ImmutableMap.of();
	}

	public I getItem(){
		return item;
	}

	public ItemStack toItemStack(int amount){
		return item.getItemStack(this, amount);
	}

	public Map<Property, ?> getProperties(){
		return properties;
	}

	public <V> V getValue(Property<V> property){
		return (V) properties.get(property);
	}

	public <V> IStateItemState withProperty(Property<V> property, V value){
		assert item.isValid(property) && property.isValid(value);
		Map map = new HashMap<>();
		map.putAll(properties);
		map.put(property, value);
		return (IStateItemState) new IStateItemState(item, map);
	}

	public interface Property<V> {

		String getName();

		boolean isValid(V value);

	}

}
