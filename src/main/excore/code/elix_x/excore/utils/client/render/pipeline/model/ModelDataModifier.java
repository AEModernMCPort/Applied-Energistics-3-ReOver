package code.elix_x.excore.utils.client.render.pipeline.model;

import java.util.Optional;
import java.util.function.Function;

import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excore.utils.client.render.model.UnpackedSimpleBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ModelDataModifier implements PipelineElement<UnpackedSimpleBakedModel, UnpackedSimpleBakedModel> {

	public static ModelDataModifier ambientOcclusion(Function<Boolean, Boolean> ambientOcclusion){
		return new ModelDataModifier(ambientOcclusion, null, null, null, null);
	}

	public static ModelDataModifier gui3d(Function<Boolean, Boolean> gui3d){
		return new ModelDataModifier(null, gui3d, null, null, null);
	}

	public static ModelDataModifier texture(Function<TextureAtlasSprite, TextureAtlasSprite> texture){
		return new ModelDataModifier(null, null, texture, null, null);
	}

	public static ModelDataModifier cameraTransforms(Function<ItemCameraTransforms, ItemCameraTransforms> cameraTransforms){
		return new ModelDataModifier(null, null, null, cameraTransforms, null);
	}

	public static ModelDataModifier itemOverrideList(Function<ItemOverrideList, ItemOverrideList> itemOverrideList){
		return new ModelDataModifier(null, null, null, null, itemOverrideList);
	}

	private final Optional<Function<Boolean, Boolean>> ambientOcclusion;
	private final Optional<Function<Boolean, Boolean>> gui3d;
	private final Optional<Function<TextureAtlasSprite, TextureAtlasSprite>> texture;
	private final Optional<Function<ItemCameraTransforms, ItemCameraTransforms>> cameraTransforms;
	private final Optional<Function<ItemOverrideList, ItemOverrideList>> itemOverrideList;

	public ModelDataModifier(Optional<Function<Boolean, Boolean>> ambientOcclusion, Optional<Function<Boolean, Boolean>> gui3d, Optional<Function<TextureAtlasSprite, TextureAtlasSprite>> texture, Optional<Function<ItemCameraTransforms, ItemCameraTransforms>> cameraTransforms, Optional<Function<ItemOverrideList, ItemOverrideList>> itemOverrideList){
		this.ambientOcclusion = ambientOcclusion;
		this.gui3d = gui3d;
		this.texture = texture;
		this.cameraTransforms = cameraTransforms;
		this.itemOverrideList = itemOverrideList;
	}

	public ModelDataModifier(Function<Boolean, Boolean> ambientOcclusion, Function<Boolean, Boolean> gui3d, Function<TextureAtlasSprite, TextureAtlasSprite> texture, Function<ItemCameraTransforms, ItemCameraTransforms> cameraTransforms, Function<ItemOverrideList, ItemOverrideList> itemOverrideList){
		this(Optional.ofNullable(ambientOcclusion), Optional.ofNullable(gui3d), Optional.ofNullable(texture), Optional.ofNullable(cameraTransforms), Optional.ofNullable(itemOverrideList));
	}

	@Override
	public UnpackedSimpleBakedModel pipe(UnpackedSimpleBakedModel in){
		ambientOcclusion.ifPresent(funct -> in.setAmbientOcclusion(funct.apply(in.isAmbientOcclusion())));
		gui3d.ifPresent(funct -> in.setGui3d(funct.apply(in.isGui3d())));
		texture.ifPresent(funct -> in.setTexture(funct.apply(in.getTexture())));
		cameraTransforms.ifPresent(funct -> in.setCameraTransforms(funct.apply(in.getCameraTransforms())));
		itemOverrideList.ifPresent(funct -> in.setItemOverrideList(funct.apply(in.getItemOverrideList())));
		return in;
	}

}
