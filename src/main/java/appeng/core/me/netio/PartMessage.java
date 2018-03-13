package appeng.core.me.netio;

import appeng.core.me.api.parts.PartPositionRotation;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PartMessage implements IMessage {

	public PartPositionRotation posRot;
	public ResourceLocation id;
	public NBTTagCompound data;

	public PartMessage(){
	}

	public PartMessage(@Nonnull PartPositionRotation posRot, @Nullable ResourceLocation id, @Nullable NBTTagCompound data){
		this.posRot = posRot;
		this.id = id;
		this.data = data;
	}

	@Override
	public void toBytes(ByteBuf buf){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("posrot", posRot.serializeNBT());
		if(id != null) nbt.setString("id", id.toString());
		if(data != null) nbt.setTag("data", data);
		ByteBufUtils.writeTag(buf, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf){
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		posRot = PartPositionRotation.fromNBT(nbt.getCompoundTag("posrot"));
		if(nbt.hasKey("id")) id = new ResourceLocation(nbt.getString("id"));
		if(nbt.hasKey("data")) data = nbt.getCompoundTag("data");
	}

}
