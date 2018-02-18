package appeng.core.me.api.parts.part;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.container.PartsAccess;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
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

	/*
	 * Interaction
	 */

	default void onPlaced(@Nullable S part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nullable EntityPlayer placer, @Nullable EnumHand hand){

	}

	default void onBroken(@Nullable S part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nullable EntityPlayer breaker){

	}

	default EnumActionResult onRightClick(@Nullable S part, @Nonnull PartsAccess.Mutable world, @Nonnull World theWorld, @Nonnull EntityPlayer placer, @Nonnull EnumHand hand){
		return EnumActionResult.PASS;
	}

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
