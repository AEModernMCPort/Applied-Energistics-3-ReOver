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
package code.elix_x.excore.utils.nbt.mbt.encoders;

import code.elix_x.excomms.reflection.ReflectionHelper;
import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import code.elix_x.excomms.reflection.ReflectionHelper.AConstructor;
import code.elix_x.excore.utils.nbt.mbt.MBT;
import code.elix_x.excore.utils.nbt.mbt.MBTIgnore;
import code.elix_x.excore.utils.nbt.mbt.NBTEncoder;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NBTClassEncoder implements NBTEncoder<Object, NBTTagCompound> {

	public static final String CLASS = "#CLASS";

	public boolean encodeClass;
	public boolean encodeFinal;
	public boolean encodeStatic;
	public boolean encodeSuper;

	public NBTClassEncoder(boolean encodeClass, boolean encodeFinal, boolean encodeStatic, boolean encodeSuper){
		this.encodeClass = encodeClass;
		this.encodeFinal = encodeFinal;
		this.encodeStatic = encodeStatic;
		this.encodeSuper = encodeSuper;
	}

	@Override
	public boolean canEncode(Object o){
		return !o.getClass().isAnnotationPresent(MBTIgnore.class);
	}

	@Override
	public boolean canDecode(NBTBase nbt, AClass<?> clazz){
		return nbt instanceof NBTTagCompound && !clazz.get().isAnnotationPresent(MBTIgnore.class);
	}

	@Override
	public NBTTagCompound toNBT(MBT mbt, Object t){
		NBTTagCompound nbt = new NBTTagCompound();
		AClass<Object> clazz = new AClass(t.getClass());
		if(encodeClass) nbt.setString(CLASS, clazz.get().getName());
		clazz.getFields().filter(f -> !(f.get().isAnnotationPresent(MBTIgnore.class) || f.get().getType().isAnnotationPresent(MBTIgnore.class))).filter(f -> nbt.hasKey(f.get().getName())).filter(f -> encodeStatic || !f.is(ReflectionHelper.Modifier.STATIC)).filter(f -> encodeFinal || f.is(ReflectionHelper.Modifier.FINAL)).forEach(f -> nbt.setTag(f.get().getName(), mbt.toNBT(f.get(t))));
		return nbt;
	}

	@Override
	public Object fromNBT(MBT mbt, NBTTagCompound nbt, AClass<Object> clazz, List<AClass<?>> tsclasses){
		AClass<Object> c = Optional.ofNullable(encodeClass ? AClass.find(nbt.getString(CLASS)).orElse(clazz) : clazz).orElseThrow(() -> new RuntimeException("NBT Class Encoder: CLASS NOT FOUND!"));
		Object o = c.getDeclaredConstructor().map(cons -> cons.setAccessible(true)).flatMap(AConstructor::newInstance).orElseThrow(() -> new RuntimeException(String.format("NBT Class Encoder: could not instantiate %s", c.get().getName())));
		c.getFields().filter(f -> !(f.get().isAnnotationPresent(MBTIgnore.class) || f.get().getType().isAnnotationPresent(MBTIgnore.class))).filter(f -> nbt.hasKey(f.get().getName())).filter(f -> encodeStatic || !f.is(ReflectionHelper.Modifier.STATIC)).filter(f -> !(encodeFinal ? f.setFinal(false) : f).is(ReflectionHelper.Modifier.FINAL)).forEach(f -> f.setAccessible(true).set(o, mbt.fromNBT(nbt.getTag(f.get().getName()), new AClass(f.get().getType()), Optional.of(f.get().getGenericType()).map(t -> t instanceof ParameterizedType ? (ParameterizedType) t : null).map(ParameterizedType::getActualTypeArguments).map(ts -> Arrays.stream(ts).filter(t -> t instanceof Class).map(t -> new AClass((Class) t)).collect(Collectors.toList())).orElse(null))));
		return o;
	}

}
