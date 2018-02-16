package appeng.core.me.api.parts.part;

import appeng.core.me.api.parts.PartPositionRotation;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface Part<P extends Part<P, S>, S extends Part.State<P, S>> extends IForgeRegistryEntry<P> {

	/**
	 * Obj root mesh file location for physical interaction
	 * @return Obj root mesh location
	 */
	@Nonnull
	ResourceLocation getRootMesh();
	void setRootMesh(ResourceLocation mesh);

	/**
	 * List of all meshes that the part <i>can</i> use for rendering. They <b>must</b> be contained in root mesh's bounds.
	 * @return all meshes the part needs baked
	 */
	@Nonnull
	default List<ResourceLocation> getMeshes(){
		return Lists.newArrayList(getRootMesh());
	}

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

		@Nonnull
		PartPositionRotation getAssignedPosRot();

		void assignPosRot(@Nonnull PartPositionRotation positionRotation);

		/**
		 * Get the mesh to render for this state
		 * @return mesh to render
		 */
		default ResourceLocation getMesh(){
			return getPart().getRootMesh();
		}

	}

}
