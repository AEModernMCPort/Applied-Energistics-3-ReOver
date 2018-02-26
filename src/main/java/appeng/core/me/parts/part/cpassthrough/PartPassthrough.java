package appeng.core.me.parts.part.cpassthrough;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetBlockUUID;
import appeng.core.me.api.network.NetworkUUID;
import appeng.core.me.api.network.block.ConnectUUID;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import appeng.core.me.api.parts.PartColor;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.network.connect.ConnectionsParams;
import appeng.core.me.parts.part.PartBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class PartPassthrough<P extends PartPassthrough<P, S>, S extends PartPassthrough.PassthroughState<P, S>> extends PartBase<P, S> {

	protected final PartColor color;
	protected final ConnectionsParams<?> connectionParams;

	public PartPassthrough(PartColor color, ConnectionsParams connectionParams){
		this.color = color;
		this.connectionParams = connectionParams;
	}

	public PartPassthrough(boolean supportsRotation, PartColor color, ConnectionsParams connectionParams){
		super(supportsRotation);
		this.color = color;
		this.connectionParams = connectionParams;
	}

	@Override
	public void onLoad(@Nullable S part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nonnull PartPositionRotation positionRotation){
		if(part.netBlock != null) part.netBlock.assignedPassthroughLoaded(part);
	}

	@Override
	public void onBroken(@Nullable S part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nullable EntityPlayer breaker){
		part.getAssignedNetBlock().ifPresent(netBlock -> netBlock.assignedPassthroughDestroed(part));
	}

	public static abstract class PassthroughState<P extends PartPassthrough<P, S>, S extends PassthroughState<P, S>> extends PartBase.StateBase<P, S> implements ConnectionPassthrough {

		public PassthroughState(P part){
			super(part);
		}

		/*
		 * Connection
		 */

		protected ConnectUUID pcUUID = new ConnectUUID();

		@Override
		public ConnectUUID getUUIDForConnectionPassthrough(){
			return pcUUID;
		}

		@Override
		public PartColor getColor(){
			return getPart().color;
		}

		@Override
		public boolean test(Connection connection){
			return getPart().connectionParams.getParam(connection) != null;
		}

		@Override
		public <Param extends Comparable<Param>> Param getPassthroughConnectionParameter(Connection<Param, ?> connection){
			return getPart().connectionParams.getParam(connection);
		}

		/*
		 * Net blocks
		 */

		protected NetBlock netBlock;

		@Override
		public Optional<NetBlock> getAssignedNetBlock(){
			return Optional.ofNullable(netBlock);
		}

		@Override
		public void assignNetBlock(@Nullable NetBlock netBlock){
			this.netBlock = netBlock;
		}

		/*
		 * IO
		 */

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("pcuuid", pcUUID.serializeNBT());
			if(netBlock != null){
				nbt.setTag("buuid", netBlock.getUUID().serializeNBT());
				netBlock.getNetwork().ifPresent(network -> nbt.setTag("nuuid", network.getUUID().serializeNBT()));
			}
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			pcUUID = ConnectUUID.fromNBT(nbt.getCompoundTag("pcuuid"));
			netBlock = AppEngME.INSTANCE.getGlobalNBDManager().getNetblock(Optional.ofNullable(nbt.hasKey("buuid") ? NetBlockUUID.fromNBT(nbt.getCompoundTag("buuid")) : null), Optional.ofNullable(nbt.hasKey("nuuid") ? NetworkUUID.fromNBT(nbt.getCompoundTag("nuuid")) : null)).orElse(null);
		}

		@Override
		public NBTTagCompound serializeSyncNBT(){
			return new NBTTagCompound();
		}

		@Override
		public void deserializeSyncNBT(NBTTagCompound nbt){

		}

	}

}
