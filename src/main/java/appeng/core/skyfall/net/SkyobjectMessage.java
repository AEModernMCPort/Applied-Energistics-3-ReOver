package appeng.core.skyfall.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public abstract class SkyobjectMessage implements IMessage {

	public UUID uuid;

	public SkyobjectMessage(){
	}

	public SkyobjectMessage(UUID uuid){
		this.uuid = uuid;
	}

	@Override
	public void toBytes(ByteBuf buf){
		long m = uuid.getMostSignificantBits();
		long l = uuid.getLeastSignificantBits();
		buf.writeLong(m).writeLong(l);
	}

	@Override
	public void fromBytes(ByteBuf buf){
		long m = buf.readLong();
		long l = buf.readLong();
		uuid = new UUID(m, l);
	}

	public static class AddOrChange extends SkyobjectMessage {

		public ResourceLocation id;
		public NBTTagCompound nbt;

		public AddOrChange(){
		}

		public AddOrChange(UUID uuid, ResourceLocation id, NBTTagCompound nbt){
			super(uuid);
			this.id = id;
			this.nbt = nbt;
		}

		@Override
		public void toBytes(ByteBuf buf){
			super.toBytes(buf);
			ByteBufUtils.writeUTF8String(buf, id.toString());
			ByteBufUtils.writeTag(buf, nbt);
		}

		@Override
		public void fromBytes(ByteBuf buf){
			super.fromBytes(buf);
			id = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
			nbt = ByteBufUtils.readTag(buf);
		}
	}

	public static class Remove extends SkyobjectMessage {

		public Remove(){
		}

		public Remove(UUID uuid){
			super(uuid);
		}

	}

}
