package appeng.core.me.api.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartUUID;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public interface PartInfo<P extends Part<P, S>, S extends Part.State<P, S>> {

	@Nonnull
	P getPart();

	@Nonnull
	PartPositionRotation getPositionRotation();

	@Nonnull
	Optional<S> getState();

	/**
	 * To ensure inter-implementation compatibility, any serializer and deserializer <b>must use following scheme</b> (additional info can be serialized considering it may be lost):
	 * <code>
	 * {
	 * 	"part": [{@link #getPart()}.{@link ResourceLocation#toString()}],
	 * 	"posRot": [{@link #getPositionRotation()}.{@link PartPositionRotation#serializeNBT()}],
	 * 	"state": [{@link #getState()}.{@link Part.State#serializeNBT()}] //Optional - not serialized if the state is {@link Optional#empty()}
	 * }
	 * </code>
	 *
	 * @return serialized part info
	 */
	@Nonnull
	default NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("part", getPart().getRegistryName().toString());
		nbt.setTag("posRot", getPositionRotation().serializeNBT());
		getState().ifPresent(state -> nbt.setTag("state", state.serializeNBT()));
		return nbt;
	}

}
