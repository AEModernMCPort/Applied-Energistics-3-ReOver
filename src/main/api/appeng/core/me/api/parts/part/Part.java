package appeng.core.me.api.parts.part;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Part<P extends Part<P, S>, S extends Part.State<P, S>> extends IForgeRegistryEntry<P> {

	/**
	 * Obj mesh file location for all the things (from rendering to raytracing & collision handling)
	 * @return Obj mesh location
	 */
	@Nonnull ResourceLocation getMesh();

	void setMesh(ResourceLocation mesh);

	/**
	 * Retrieves unlocalized name of the parts
	 * @return unlocalized name of the parts
	 */
	@Nullable String getUnlocalizedName();

	void setUnlocalizedName(String unlocalizedName);

	default boolean supportsRotation(){
		return true;
	}

	/**
	 * Creates new instance of parts state for data storage
	 * @return new instance of parts state
	 */
	S createNewState();

	interface State<P extends Part<P, S>, S extends State<P, S>> extends INBTSerializable<NBTTagCompound> {

		P getPart();

	}

}
