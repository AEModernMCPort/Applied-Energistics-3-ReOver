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
package code.elix_x.excore.utils.net.packets;

import code.elix_x.excore.utils.nbt.mbt.MBT;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class SmartMessage<T> implements IMessage {

	public static final MBT mbt = new MBT();

	public T t;

	public SmartMessage(T t){
		this.t = t;
	}

	public MBT getMBT(){
		return mbt;
	}

	public abstract Class<? extends T> getTClass();

	@Override
	public void toBytes(ByteBuf buf){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("data", getMBT().toNBT(t));
		ByteBufUtils.writeTag(buf, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf){
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		t = getMBT().fromNBT(nbt.getTag("data"), getTClass());
	}

}
