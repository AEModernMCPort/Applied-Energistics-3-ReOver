package appeng.core.me.parts.part;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.container.PartUUID;
import appeng.core.me.api.parts.part.Part;
import code.elix_x.excomms.reflection.ReflectionHelper;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class PartBase<P extends PartBase<P, S>, S extends PartBase.StateBase<P, S>> extends IForgeRegistryEntry.Impl<P> implements Part<P, S> {

	private static final ReflectionHelper.AField<Impl, TypeToken> token = new ReflectionHelper.AClass<>(IForgeRegistryEntry.Impl.class).<TypeToken>getDeclaredField("token").orElseThrow(() -> new IllegalArgumentException("Could not reflect fields necessary for parts registration")).setAccessible(true);
	private static final TypeToken<Part> partToken = TypeToken.of(Part.class);

	protected String unlocalizedName;
	protected ResourceLocation rootMesh;
	protected List<ResourceLocation> meshes;

	protected boolean supportsRotation = true;

	public PartBase(){
		token.set(this, partToken);
	}

	public PartBase(boolean supportsRotation){
		this();
		this.supportsRotation = supportsRotation;
	}

	@Nullable
	@Override
	public String getUnlocalizedName(){
		return unlocalizedName;
	}

	public void setUnlocalizedName(String unlocalizedName){
		this.unlocalizedName = unlocalizedName;
	}

	@Nonnull
	@Override
	public ResourceLocation getRootMesh(){
		return rootMesh;
	}

	public void setRootMesh(ResourceLocation mesh){
		this.rootMesh = mesh;
		this.meshes = Lists.newArrayList(rootMesh);
	}

	@Nonnull
	@Override
	public List<ResourceLocation> getMeshes(){
		return meshes;
	}

	@Override
	public boolean supportsRotation(){
		return supportsRotation;
	}

	public static abstract class StateBase<P extends PartBase<P, S>, S extends PartBase.StateBase<P, S>> implements Part.State<P, S> {

		private final P part;

		public StateBase(P part){
			this.part = part;
		}

		@Override
		public P getPart(){
			return part;
		}

		private transient PartUUID uuid;
		private transient PartPositionRotation positionRotation;

		@Nonnull
		@Override
		public PartUUID getAssignedUUID(){
			return uuid;
		}

		@Nonnull
		@Override
		public PartPositionRotation getAssignedPosRot(){
			return positionRotation;
		}

		@Override
		public void assignInfo(@Nonnull PartUUID uuid, @Nonnull PartPositionRotation positionRotation){
			this.uuid = uuid;
			this.positionRotation = positionRotation;
		}

	}

}
