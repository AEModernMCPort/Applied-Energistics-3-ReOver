package appeng.core.me.api.client.part;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.client.renderer.block.model.BakedQuad;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public interface PartRenderingHandler<P extends Part<P, S>, S extends Part.State<P, S>> {

	Collection<BakedQuad> getQuads(@Nullable S state, @Nonnull PartPositionRotation partPositionRotation);

	default Optional<Dynamic<P, S>> createDynamicRH(@Nonnull S part){
		return Optional.empty();
	}

	interface Dynamic<P extends Part<P, S>, S extends Part.State<P, S>> {

		default void init(){}

		void render(float partialTicks);

		default void cleanup(){}

	}

}
