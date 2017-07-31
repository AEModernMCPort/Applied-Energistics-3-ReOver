package appeng.core.me.parts.part;

import appeng.core.me.api.parts.part.Part;
import code.elix_x.excomms.reflection.ReflectionHelper;
import com.google.common.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PartBase<P extends PartBase<P, S>, S extends PartBase.StateBase<P, S>> extends IForgeRegistryEntry.Impl<P> implements Part<P, S> {

	private static final ReflectionHelper.AField<Impl, TypeToken> token = new ReflectionHelper.AClass<>(IForgeRegistryEntry.Impl.class).<TypeToken>getDeclaredField("token").setAccessible(true);
	private static final TypeToken<Part> partToken = TypeToken.of(Part.class);

	protected String unlocalizedName;
	protected ResourceLocation mesh;

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
	public ResourceLocation getMesh(){
		return mesh;
	}

	public void setMesh(ResourceLocation mesh){
		this.mesh = mesh;
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

	}

}
