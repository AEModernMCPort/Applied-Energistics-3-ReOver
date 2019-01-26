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

import code.elix_x.excomms.reflection.ReflectionHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MBT {

	public static final NBTEncoder[] DEFAULTENCODERS = new NBTEncoder[]{NBTEncoder.booleanEncoder, NBTEncoder.byteEncoder, NBTEncoder.shortEncoder, NBTEncoder.intEncoder, NBTEncoder.longEncoder, NBTEncoder.floatEncoder, NBTEncoder.doubleEncoder, NBTEncoder.byteArrayEncoder, NBTEncoder.intArrayEncoder, NBTEncoder.stringEncoder, NBTEncoder.nbtEncoder, NBTEncoder.itemStackEncoder, NBTEncoder.uuidEncoder, NBTEncoder.enumEncoder, NBTEncoder.nullEncoder, NBTEncoder.arrayEncoder, NBTEncoder.listEncoder, NBTEncoder.setEncoder, NBTEncoder.mapEncoder, NBTEncoder.multimapEncoder, NBTEncoder.classEncoder};

	public static final NBTEncoder[] DEFAULTSPECIFICENCODERS = new NBTEncoder[]{NBTEncoder.booleanEncoder, NBTEncoder.byteEncoder, NBTEncoder.shortEncoder, NBTEncoder.intEncoder, NBTEncoder.longEncoder, NBTEncoder.floatEncoder, NBTEncoder.doubleEncoder, NBTEncoder.byteArrayEncoder, NBTEncoder.intArrayEncoder, NBTEncoder.stringEncoder, NBTEncoder.nbtEncoder, NBTEncoder.itemStackEncoder, NBTEncoder.uuidEncoder, NBTEncoder.enumEncoder, NBTEncoder.nullEncoder, NBTEncoder.arrayEncoder, NBTEncoder.listEncoder, NBTEncoder.setEncoder, NBTEncoder.mapEncoder, NBTEncoder.multimapEncoder};

	public static final NBTEncoder[] PRIMITIVEENCODERS = new NBTEncoder[]{NBTEncoder.booleanEncoder, NBTEncoder.byteEncoder, NBTEncoder.shortEncoder, NBTEncoder.intEncoder, NBTEncoder.longEncoder, NBTEncoder.floatEncoder, NBTEncoder.doubleEncoder};

	public static final NBTEncoder[] OBJECTSPECIFICENCODERS = new NBTEncoder[]{NBTEncoder.stringEncoder, NBTEncoder.nbtEncoder, NBTEncoder.itemStackEncoder, NBTEncoder.uuidEncoder, NBTEncoder.enumEncoder, NBTEncoder.nullEncoder};

	public static final NBTEncoder[] ITERABLEENCODERS = new NBTEncoder[]{NBTEncoder.byteArrayEncoder, NBTEncoder.intArrayEncoder, NBTEncoder.arrayEncoder, NBTEncoder.listEncoder, NBTEncoder.setEncoder};

	public static final NBTEncoder[] MAPENCODERS = new NBTEncoder[]{NBTEncoder.mapEncoder, NBTEncoder.multimapEncoder};

	public static final NBTEncoder[] DIRECTNBTENCODERS = new NBTEncoder[]{NBTEncoder.booleanEncoder, NBTEncoder.byteEncoder, NBTEncoder.shortEncoder, NBTEncoder.intEncoder, NBTEncoder.longEncoder, NBTEncoder.floatEncoder, NBTEncoder.doubleEncoder, NBTEncoder.byteArrayEncoder, NBTEncoder.intArrayEncoder, NBTEncoder.stringEncoder};

	private List<NBTEncoder> encoders;

	public MBT(){
		this(DEFAULTENCODERS);
	}

	public MBT(NBTEncoder... encoders){
		this.encoders = Lists.newArrayList(encoders);
	}

	public MBT(List<NBTEncoder> encoders){
		this.encoders = encoders;
	}

	public <T> NBTBase toNBT(@Nullable T t){
		for(NBTEncoder encoder : encoders){
			if(encoder.canEncode(t)){
				return encoder.toNBT(this, t);
			}
		}
		return null;
	}

	public <T> T fromNBT(@Nonnull NBTBase nbt, @Nonnull ReflectionHelper.AClass<T> clazz, @Nullable List<ReflectionHelper.AClass> tsclasses){
		for(NBTEncoder encoder : encoders){
			if(encoder.canDecode(nbt, clazz)){
				return (T) encoder.fromNBT(this, nbt, clazz, tsclasses);
			}
		}
		return null;
	}

	public <T> T fromNBT(@Nonnull NBTBase nbt, @Nonnull ReflectionHelper.AClass<T> clazz, ReflectionHelper.AClass... tsclasses){
		return fromNBT(nbt, clazz, ImmutableList.copyOf(tsclasses));
	}

	@Deprecated
	public <T> T fromNBT(NBTBase nbt, Class<T> clazz, Class... tsclasses){
		return fromNBT(nbt, new ReflectionHelper.AClass<>(clazz), tsclasses == null ? null : Arrays.stream((Class<?>[]) tsclasses).map(c -> new ReflectionHelper.AClass(c)).collect(Collectors.toList()).toArray(new ReflectionHelper.AClass[tsclasses.length]));
	}

}
