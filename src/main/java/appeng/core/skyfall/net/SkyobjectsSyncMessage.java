package appeng.core.skyfall.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SkyobjectsSyncMessage implements IMessage {

	public NBTTagCompound nbt;

	public SkyobjectsSyncMessage(){
	}

	public SkyobjectsSyncMessage(NBTTagCompound nbt){
		this.nbt = nbt;
	}

	@Override
	public void toBytes(ByteBuf buf){
		ByteBufUtils.writeTag(buf, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf){
		nbt = ByteBufUtils.readTag(buf);
	}

}
