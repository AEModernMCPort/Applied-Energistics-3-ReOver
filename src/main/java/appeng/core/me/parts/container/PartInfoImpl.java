package appeng.core.me.parts.container;

import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
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
		PartPositionRotation positionRotation = PartPositionRotation.fromNBT(nbt.getCompoundTag("posRot"));
		P part = AppEngME.INSTANCE.<P, S>getPartRegistry().getValue(new ResourceLocation(nbt.getString("part")));
		S state = null;
		if(nbt.hasKey("state")){
			(state = part.createNewState()).deserializeNBT(nbt.getCompoundTag("state"));
			state.assignPosRot(positionRotation);
		}
		return new PartInfoImpl<>(part, positionRotation, state);
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof PartInfo)) return false;
		PartInfo<?, ?> partInfo = (PartInfo<?, ?>) o;
		return Objects.equals(getPart(), partInfo.getPart()) && Objects.equals(getPositionRotation(), partInfo.getPositionRotation()) && Objects.equals(getState(), partInfo.getState());
	}

	@Override
	public int hashCode(){
		return Objects.hash(getPart(), getPositionRotation(), getState());
	}

}
