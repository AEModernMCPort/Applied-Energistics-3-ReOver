package appeng.core.me.api.parts.part;

import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import com.owens.oobjloader.builder.Mesh;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface PartsHelper {

	@Nonnull
	PartPlacementLogic createDefaultPlacementLogic(@Nonnull Part part);

	<T> void registerCustomPartDataLoader(@Nonnull ResourceLocation id, @Nonnull CustomPartDataLoader<T> loader);

	@Nonnull
	<T> Optional<T> getCustomPartData(@Nonnull Part part, @Nonnull ResourceLocation id);

	interface CustomPartDataLoader<T> {

		Function<ResourceLocation, String> DEFID2SUFFIX = id -> id.toString().replace(":", "-_-").replace("/", "_-");

		@Nonnull
		Optional<T> load(@Nonnull Part part, @Nonnull Function<String, Optional<Mesh>> meshLoader, @Nonnull Function<Mesh, Set<VoxelPosition>> voxelizer, @Nonnull Set<VoxelPosition> rootMeshVoxels);

	}

}
