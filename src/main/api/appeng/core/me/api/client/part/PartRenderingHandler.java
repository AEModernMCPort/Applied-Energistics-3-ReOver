package appeng.core.me.api.client.part;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.Collection;

public interface PartRenderingHandler<P extends Part<P, S>, S extends Part.State<P, S>> {

	Collection<BakedQuad> getQuads(S state, PartPositionRotation partPositionRotation);

}
