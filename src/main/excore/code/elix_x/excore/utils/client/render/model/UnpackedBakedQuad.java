package code.elix_x.excore.utils.client.render.model;

import org.lwjgl.opengl.GL11;

import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import code.elix_x.excomms.reflection.ReflectionHelper.AField;
import code.elix_x.excore.utils.client.render.model.vertex.DefaultUnpackedVertices;
import code.elix_x.excore.utils.client.render.model.vertex.PackedVertices;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder;

public class UnpackedBakedQuad {

	private DefaultUnpackedVertices vertices;
	private int tintIndex;
	private EnumFacing face;
	private TextureAtlasSprite sprite;
	private boolean applyDiffuseLighting;

	public UnpackedBakedQuad(DefaultUnpackedVertices vertices, int tintIndex, EnumFacing face, TextureAtlasSprite sprite, boolean applyDiffuseLighting){
		this.vertices = vertices;
		this.tintIndex = tintIndex;
		this.face = face;
		this.sprite = sprite;
		this.applyDiffuseLighting = applyDiffuseLighting;
	}

	public UnpackedBakedQuad(PackedVertices vertices, int tintIndex, EnumFacing face, TextureAtlasSprite sprite, boolean applyDiffuseLighting){
		this(vertices.unpack(), tintIndex, face, sprite, applyDiffuseLighting);
	}

	public UnpackedBakedQuad(float[][][] vertexData, VertexFormat format, int tintIndex, EnumFacing face, TextureAtlasSprite sprite, boolean applyDiffuseLighting){
		this(new PackedVertices(GL11.GL_QUADS, format, vertexData), tintIndex, face, sprite, applyDiffuseLighting);
	}

	public DefaultUnpackedVertices getVertices(){
		return vertices;
	}

	public void setVertices(DefaultUnpackedVertices vertices){
		this.vertices = vertices;
	}

	public int getTintIndex(){
		return tintIndex;
	}

	public void setTintIndex(int tintIndex){
		this.tintIndex = tintIndex;
	}

	public EnumFacing getFace(){
		return face;
	}

	public void setFace(EnumFacing face){
		this.face = face;
	}

	public TextureAtlasSprite getSprite(){
		return sprite;
	}

	public void setSprite(TextureAtlasSprite sprite){
		this.sprite = sprite;
	}

	public boolean isApplyDiffuseLighting(){
		return applyDiffuseLighting;
	}

	public void setApplyDiffuseLighting(boolean applyDiffuseLighting){
		this.applyDiffuseLighting = applyDiffuseLighting;
	}

	public BakedQuad pack(VertexFormat format){
		return new net.minecraftforge.client.model.pipeline.UnpackedBakedQuad(vertices.pack(GL11.GL_QUADS, format).getData(), tintIndex, face, sprite, applyDiffuseLighting, format);
	}

	private static final AField<net.minecraftforge.client.model.pipeline.UnpackedBakedQuad, float[][][]> unpackedData = new AClass<>(net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.class).<float[][][]>getDeclaredField("unpackedData").orElseThrow(() -> new IllegalArgumentException("Failed to reflect forge.UnpackedBakedQuad#unpackedData necessary for excore.UnpackedBakedQuad")).setAccessible(true);

	public static UnpackedBakedQuad unpack(net.minecraftforge.client.model.pipeline.UnpackedBakedQuad quad){
		return new UnpackedBakedQuad(unpackedData.get(quad).get(), quad.getFormat(), quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting());
	}

	// TODO - Move to functional models utils class once created.
	@Deprecated
	public static net.minecraftforge.client.model.pipeline.UnpackedBakedQuad unpackForge(BakedQuad quad){
		if(quad instanceof net.minecraftforge.client.model.pipeline.UnpackedBakedQuad)
			return (net.minecraftforge.client.model.pipeline.UnpackedBakedQuad) quad;
		Builder builder = new Builder(quad.getFormat());
		quad.pipe(builder);
		return builder.build();
	}

	public static UnpackedBakedQuad unpack(BakedQuad quad){
		return unpack(unpackForge(quad));
	}

}
