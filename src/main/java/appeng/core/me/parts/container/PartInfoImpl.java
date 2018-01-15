package appeng.core.me.parts.container;

import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartUUID;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public final class PartInfoImpl<P extends Part<P, S>, S extends Part.State<P, S>> implements PartInfo<P, S> {

	private final P part;
	private final PartPositionRotation positionRotation;
	private final S state;

	public PartInfoImpl(@Nonnull P part, @Nonnull PartPositionRotation positionRotation, @Nullable S state){
		this.part = part;
		this.positionRotation = positionRotation;
		this.state = state;
	}

	@Nonnull
	@Override
	public P getPart(){
		return part;
	}

	@Nonnull
	@Override
	public PartPositionRotation getPositionRotation(){
		return positionRotation;
	}

	@Nonnull
	@Override
	public Optional<S> getState(){
		return Optional.ofNullable(state);
	}

	@Nonnull
	public static <P extends Part<P, S>, S extends Part.State<P, S>> PartInfo<P, S> deserializeNBT(NBTTagCompound nbt){
		P part = AppEngME.INSTANCE.<P, S>getPartRegistry().getValue(new ResourceLocation(nbt.getString("part")));
		S state = null;
		if(nbt.hasKey("state")) (state = part.createNewState()).deserializeNBT(nbt.getCompoundTag("state"));
		return new PartInfoImpl<>(part, PartPositionRotation.fromNBT(nbt.getCompoundTag("posRot")), state);
	}

}
