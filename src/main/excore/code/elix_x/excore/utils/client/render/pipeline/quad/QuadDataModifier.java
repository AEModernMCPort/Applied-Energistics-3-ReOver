package code.elix_x.excore.utils.client.render.pipeline.quad;

import java.util.Optional;
import java.util.function.Function;

import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excore.utils.client.render.model.UnpackedBakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class QuadDataModifier implements PipelineElement<UnpackedBakedQuad, UnpackedBakedQuad> {

	public static QuadDataModifier tintIndex(Function<Integer, Integer> tintIndex){
		return new QuadDataModifier(tintIndex, null, null, null);
	}

	public static QuadDataModifier face(Function<EnumFacing, EnumFacing> face){
		return new QuadDataModifier(null, face, null, null);
	}

	public static QuadDataModifier sprite(Function<TextureAtlasSprite, TextureAtlasSprite> sprite){
		return new QuadDataModifier(null, null, sprite, null);
	}

	public static QuadDataModifier applyDiffuseLighting(Function<Boolean, Boolean> applyDiffuseLighting){
		return new QuadDataModifier(null, null, null, applyDiffuseLighting);
	}

	private final Optional<Function<Integer, Integer>> tintIndex;
	private final Optional<Function<EnumFacing, EnumFacing>> face;
	private final Optional<Function<TextureAtlasSprite, TextureAtlasSprite>> sprite;
	private final Optional<Function<Boolean, Boolean>> applyDiffuseLighting;

	public QuadDataModifier(Optional<Function<Integer, Integer>> tintIndex, Optional<Function<EnumFacing, EnumFacing>> face, Optional<Function<TextureAtlasSprite, TextureAtlasSprite>> sprite, Optional<Function<Boolean, Boolean>> applyDiffuseLighting){
		this.tintIndex = tintIndex;
		this.face = face;
		this.sprite = sprite;
		this.applyDiffuseLighting = applyDiffuseLighting;
	}

	public QuadDataModifier(Function<Integer, Integer> tintIndex, Function<EnumFacing, EnumFacing> face, Function<TextureAtlasSprite, TextureAtlasSprite> sprite, Function<Boolean, Boolean> applyDiffuseLighting){
		this(Optional.ofNullable(tintIndex), Optional.ofNullable(face), Optional.ofNullable(sprite), Optional.ofNullable(applyDiffuseLighting));
	}

	@Override
	public UnpackedBakedQuad pipe(UnpackedBakedQuad in){
		tintIndex.ifPresent(funct -> in.setTintIndex(funct.apply(in.getTintIndex())));
		face.ifPresent(funct -> in.setFace(funct.apply(in.getFace())));
		sprite.ifPresent(funct -> in.setSprite(funct.apply(in.getSprite())));
		applyDiffuseLighting.ifPresent(funct -> in.setApplyDiffuseLighting(funct.apply(in.isApplyDiffuseLighting())));
		return in;
	}

}
