/*******************************************************************************
 * Copyright 2016 Elix_x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package code.elix_x.excore.utils.nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.function.Function;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTMapUtils {

	public static <K, V> NBTTagList toNbt(Map<K, V> map, Function<K, ? extends NBTBase> keyToNBT, Function<V, ? extends NBTBase> valueToNBT){
		NBTTagList list = new NBTTagList();
		for(Entry<K, V> entry : map.entrySet()){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("key", keyToNBT.apply(entry.getKey()));
			tag.setTag("value", valueToNBT.apply(entry.getValue()));
			list.appendTag(tag);
		}
		return list;
	}

	public static <K, V> Map<K, V> fromNbt(NBTTagList list, Function<NBTBase, K> nbtToKey, Function<NBTBase, V> nbtToValue){
		return fromNbt(new HashMap<K, V>(), list, nbtToKey, nbtToValue);
	}

	public static <K, V> Map<K, V> fromNbt(Map<K, V> map, NBTTagList list, Function<NBTBase, K> nbtToKey, Function<NBTBase, V> nbtToValue){
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			map.put(nbtToKey.apply(tag.getTag("key")), nbtToValue.apply(tag.getTag("value")));
		}
		return map;
	}
}
