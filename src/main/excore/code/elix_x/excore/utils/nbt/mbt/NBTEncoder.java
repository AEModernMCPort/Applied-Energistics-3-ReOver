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
package code.elix_x.excore.utils.nbt.mbt;

import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import code.elix_x.excomms.reflection.ReflectionHelper.AConstructor;
import code.elix_x.excore.utils.nbt.mbt.encoders.NBTClassEncoder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

public interface NBTEncoder<T, NT extends NBTBase> {

	NBTEncoder<Boolean, NBTTagByte> booleanEncoder = new NBTEncoder<Boolean, NBTTagByte>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Boolean;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagByte && (Boolean.class.isAssignableFrom(clazz.get()) || boolean.class.isAssignableFrom(clazz.get()));
		}

		@Override
		public NBTTagByte toNBT(MBT mbt, Boolean b){
			return new NBTTagByte((byte) (b ? 1 : 0));
		}

		@Override
		public Boolean fromNBT(MBT mbt, NBTTagByte nbt, AClass<Boolean> clazz, List<AClass<?>> tsclasses){
			return nbt.getByte() == 1;
		}

	};

	NBTEncoder<Byte, NBTTagByte> byteEncoder = new NBTEncoder<Byte, NBTTagByte>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Byte;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagByte;
		}

		@Override
		public NBTTagByte toNBT(MBT mbt, Byte b){
			return new NBTTagByte(b);
		}

		@Override
		public Byte fromNBT(MBT mbt, NBTTagByte nbt, AClass<Byte> clazz, List<AClass<?>> tsclasses){
			return nbt.getByte();
		}

	};

	NBTEncoder<Short, NBTTagShort> shortEncoder = new NBTEncoder<Short, NBTTagShort>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Short;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagShort;
		}

		@Override
		public NBTTagShort toNBT(MBT mbt, Short s){
			return new NBTTagShort(s);
		}

		@Override
		public Short fromNBT(MBT mbt, NBTTagShort nbt, AClass<Short> clazz, List<AClass<?>> tsclasses){
			return nbt.getShort();
		}

	};

	NBTEncoder<Integer, NBTTagInt> intEncoder = new NBTEncoder<Integer, NBTTagInt>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Integer;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagInt;
		}

		@Override
		public NBTTagInt toNBT(MBT mbt, Integer i){
			return new NBTTagInt(i);
		}

		@Override
		public Integer fromNBT(MBT mbt, NBTTagInt nbt, AClass<Integer> clazz, List<AClass<?>> tsclasses){
			return nbt.getInt();
		}

	};

	NBTEncoder<Long, NBTTagLong> longEncoder = new NBTEncoder<Long, NBTTagLong>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Long;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagLong;
		}

		@Override
		public NBTTagLong toNBT(MBT mbt, Long l){
			return new NBTTagLong(l);
		}

		@Override
		public Long fromNBT(MBT mbt, NBTTagLong nbt, AClass<Long> clazz, List<AClass<?>> tsclasses){
			return nbt.getLong();
		}

	};

	NBTEncoder<Float, NBTTagFloat> floatEncoder = new NBTEncoder<Float, NBTTagFloat>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Float;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagFloat;
		}

		@Override
		public NBTTagFloat toNBT(MBT mbt, Float f){
			return new NBTTagFloat(f);
		}

		@Override
		public Float fromNBT(MBT mbt, NBTTagFloat nbt, AClass<Float> clazz, List<AClass<?>> tsclasses){
			return nbt.getFloat();
		}

	};

	NBTEncoder<Double, NBTTagDouble> doubleEncoder = new NBTEncoder<Double, NBTTagDouble>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Double;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagDouble;
		}

		@Override
		public NBTTagDouble toNBT(MBT mbt, Double d){
			return new NBTTagDouble(d);
		}

		@Override
		public Double fromNBT(MBT mbt, NBTTagDouble nbt, AClass<Double> clazz, List<AClass<?>> tsclasses){
			return nbt.getDouble();
		}

	};

	NBTEncoder<byte[], NBTTagByteArray> byteArrayEncoder = new NBTEncoder<byte[], NBTTagByteArray>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof byte[];
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagByteArray;
		}

		@Override
		public NBTTagByteArray toNBT(MBT mbt, byte[] bs){
			return new NBTTagByteArray(bs);
		}

		@Override
		public byte[] fromNBT(MBT mbt, NBTTagByteArray nbt, AClass<byte[]> clazz, List<AClass<?>> tsclasses){
			return nbt.getByteArray();
		}

	};

	NBTEncoder<int[], NBTTagIntArray> intArrayEncoder = new NBTEncoder<int[], NBTTagIntArray>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof int[];
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagIntArray;
		}

		@Override
		public NBTTagIntArray toNBT(MBT mbt, int[] is){
			return new NBTTagIntArray(is);
		}

		@Override
		public int[] fromNBT(MBT mbt, NBTTagIntArray nbt, AClass<int[]> clazz, List<AClass<?>> tsclasses){
			return nbt.getIntArray();
		}

	};

	NBTEncoder<String, NBTTagString> stringEncoder = new NBTEncoder<String, NBTTagString>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof String;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagString && String.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTTagString toNBT(MBT mbt, String s){
			return new NBTTagString(s);
		}

		@Override
		public String fromNBT(MBT mbt, NBTTagString nbt, AClass<String> clazz, List<AClass<?>> tsclasses){
			return nbt.getString();
		}

	};

	NBTEncoder<NBTBase, NBTBase> nbtEncoder = new NBTEncoder<NBTBase, NBTBase>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof NBTBase;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return NBTBase.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTBase toNBT(MBT mbt, NBTBase nbt){
			return nbt;
		}

		@Override
		public NBTBase fromNBT(MBT mbt, NBTBase nbt, AClass<NBTBase> clazz, List<AClass<?>> tsclasses){
			return nbt;
		}

	};

	NBTEncoder<ItemStack, NBTTagCompound> itemStackEncoder = new NBTEncoder<ItemStack, NBTTagCompound>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof ItemStack;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return ItemStack.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTTagCompound toNBT(MBT mbt, ItemStack itemstack){
			return itemstack.writeToNBT(new NBTTagCompound());
		}

		@Override
		public ItemStack fromNBT(MBT mbt, NBTTagCompound nbt, AClass<ItemStack> clazz, List<AClass<?>> tsclasses){
			return new ItemStack(nbt);
		}

	};

	NBTEncoder<UUID, NBTTagString> uuidEncoder = new NBTEncoder<UUID, NBTTagString>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof UUID;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagString && UUID.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTTagString toNBT(MBT mbt, UUID uuid){
			return new NBTTagString(uuid.toString());
		}

		@Override
		public UUID fromNBT(MBT mbt, NBTTagString nbt, AClass<UUID> clazz, List<AClass<?>> tsclasses){
			return UUID.fromString(nbt.getString());
		}

	};

	NBTEncoder<Enum, NBTTagString> enumEncoder = new NBTEncoder<Enum, NBTTagString>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Enum;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagString && clazz.isEnum();
		}

		@Override
		public NBTTagString toNBT(MBT mbt, Enum e){
			return new NBTTagString(e.name());
		}

		@Override
		public Enum fromNBT(MBT mbt, NBTTagString nbt, AClass<Enum> clazz, List<AClass<?>> tsclasses){
			return clazz.asEnum().getEnum(nbt.getString());
		}

	};

	NBTEncoder<Object, NBTTagCompound> nullEncoder = new NBTEncoder<Object, NBTTagCompound>(){

		public static final String NULL = "#NULL";

		@Override
		public boolean canEncode(Object o){
			return o == null;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagCompound && ((NBTTagCompound) nbt).hasKey(NULL) && ((NBTTagCompound) nbt).getString(NULL).equals(NULL);
		}

		@Override
		public NBTTagCompound toNBT(MBT mbt, Object t){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString(NULL, NULL);
			return nbt;
		}

		@Override
		public Object fromNBT(MBT mbt, NBTTagCompound nbt, AClass<Object> clazz, List<AClass<?>> tsclasses){
			return null;
		}

	};

	NBTEncoder<Optional<?>, NBTTagCompound> optionalEncoder = new NBTEncoder<Optional<?>, NBTTagCompound>() {

		public static final String OPTIONAL = "#OPTIONAL";

		@Override
		public boolean canEncode(Object o){
			return o instanceof Optional;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagCompound && Optional.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTTagCompound toNBT(MBT mbt, Optional<?> o){
			NBTTagCompound nbt = new NBTTagCompound();
			o.ifPresent(v -> nbt.setTag(OPTIONAL, mbt.toNBT(v)));
			return nbt;
		}

		@Override
		public Optional fromNBT(MBT mbt, NBTTagCompound nbt, AClass<Optional<?>> clazz, List<AClass<?>> tsclasses){
			if(nbt.hasKey(OPTIONAL)) return Optional.of(mbt.fromNBT(nbt.getTag(OPTIONAL), tsclasses.get(0)));
			return Optional.empty();
		}

	};

	NBTEncoder<Object[], NBTTagList> arrayEncoder = new NBTEncoder<Object[], NBTTagList>(){

		@Override
		public boolean canEncode(Object o){
			return o.getClass().isArray();
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagList && clazz.get().isArray();
		}

		@Override
		public NBTTagList toNBT(MBT mbt, Object[] os){
			NBTTagList nlist = new NBTTagList();
			for(Object o : os){
				nlist.appendTag(mbt.toNBT(o));
			}
			return nlist;
		}

		@Override
		public Object[] fromNBT(MBT mbt, NBTTagList list, AClass<Object[]> clazz,List<AClass<?>> tsclasses){
			AClass<?> comp = new AClass(clazz.get().getComponentType());
			Object[] os = (Object[]) Array.newInstance(comp.get(), 0);
			for(int i = 0; i < list.tagCount(); i++){
				os = ArrayUtils.add(os, mbt.fromNBT(list.get(i), comp));
			}
			return os;
		}

	};

	NBTEncoder<List<?>, NBTTagList> listEncoder = new NBTEncoder<List<?>, NBTTagList>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof List;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagList && List.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTTagList toNBT(MBT mbt, List<?> list){
			NBTTagList nlist = new NBTTagList();
			for(Object o : list){
				nlist.appendTag(mbt.toNBT(o));
			}
			return nlist;
		}

		@Override
		public List<?> fromNBT(MBT mbt, NBTTagList nlist, AClass<List<?>> clazz, List<AClass<?>> tsclasses){
			List list = clazz.getDeclaredConstructor().flatMap(AConstructor::newInstance).orElseGet(ArrayList::new);
			for(int i = 0; i < nlist.tagCount(); i++){
				list.add(mbt.fromNBT(nlist.get(i), tsclasses.get(0)));
			}
			return list;
		}

	};

	NBTEncoder<Set<?>, NBTTagList> setEncoder = new NBTEncoder<Set<?>, NBTTagList>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Set;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagList && Set.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTTagList toNBT(MBT mbt, Set<?> set){
			NBTTagList list = new NBTTagList();
			for(Object o : set){
				list.appendTag(mbt.toNBT(o));
			}
			return list;
		}

		@Override
		public Set<Object> fromNBT(MBT mbt, NBTTagList list, AClass<Set<?>> clazz, List<AClass<?>> tsclasses){
			Set set = clazz.getDeclaredConstructor().flatMap(AConstructor::newInstance).orElseGet(HashSet::new);
			for(int i = 0; i < list.tagCount(); i++){
				set.add(mbt.fromNBT(list.get(i), tsclasses.get(0)));
			}
			return set;
		}

	};

	NBTEncoder<Map<?, ?>, NBTTagList> mapEncoder = new NBTEncoder<Map<?, ?>, NBTTagList>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Map;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagList && Map.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTTagList toNBT(MBT mbt, Map<?, ?> map){
			NBTTagList list = new NBTTagList();
			for(Entry<?, ?> e : map.entrySet()){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("key", mbt.toNBT(e.getKey()));
				tag.setTag("value", mbt.toNBT(e.getValue()));
				list.appendTag(tag);
			}
			return list;
		}

		@Override
		public Map<?, ?> fromNBT(MBT mbt, NBTTagList list, AClass<Map<?, ?>> clazz, List<AClass<?>> tsclasses){
			Map map = clazz.getDeclaredConstructor().flatMap(AConstructor::newInstance).orElseGet(HashMap::new);
			for(int i = 0; i < list.tagCount(); i++){
				NBTTagCompound tag = list.getCompoundTagAt(i);
				map.put(mbt.fromNBT(tag.getTag("key"), tsclasses.get(0)), mbt.fromNBT(tag.getTag("value"), tsclasses.get(1)));
			}
			return map;
		}

	};

	NBTEncoder<Multimap<?, ?>, NBTTagList> multimapEncoder = new NBTEncoder<Multimap<?, ?>, NBTTagList>(){

		@Override
		public boolean canEncode(Object o){
			return o instanceof Multimap;
		}

		@Override
		public boolean canDecode(NBTBase nbt, AClass<?> clazz){
			return nbt instanceof NBTTagList && Multimap.class.isAssignableFrom(clazz.get());
		}

		@Override
		public NBTTagList toNBT(MBT mbt, Multimap<?, ?> map){
			NBTTagList list = new NBTTagList();
			for(Entry<?, ?> e : map.entries()){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("key", mbt.toNBT(e.getKey()));
				tag.setTag("value", mbt.toNBT(e.getValue()));
				list.appendTag(tag);
			}
			return list;
		}

		@Override
		public Multimap<?, ?> fromNBT(MBT mbt, NBTTagList list, AClass<Multimap<?, ?>> clazz, List<AClass<?>> tsclasses){
			Multimap map = clazz.getDeclaredConstructor().flatMap(AConstructor::newInstance).orElseGet(HashMultimap::create);
			for(int i = 0; i < list.tagCount(); i++){
				NBTTagCompound tag = list.getCompoundTagAt(i);
				map.put(mbt.fromNBT(tag.getTag("key"), tsclasses.get(0)), mbt.fromNBT(tag.getTag("value"), tsclasses.get(1)));
			}
			return map;
		}

	};

	NBTEncoder<? extends Object, NBTTagCompound> classEncoder = new NBTClassEncoder(false, false, false, false);

	@Deprecated
	NBTEncoder<? extends Object, NBTTagCompound> classEncoderSt = new NBTClassEncoder(false, false, true, false);

	@Deprecated
	NBTEncoder<? extends Object, NBTTagCompound> classEncoderSu = new NBTClassEncoder(false, false, false, true);

	@Deprecated
	NBTEncoder<? extends Object, NBTTagCompound> classEncoderStSu = new NBTClassEncoder(false, false, true, true);

	boolean canEncode(Object o);

	boolean canDecode(NBTBase nbt, AClass<?> clazz);

	NT toNBT(MBT mbt, T t);

	T fromNBT(MBT mbt, NT nbt, AClass<T> clazz, List<AClass<?>> tsclasses);

}