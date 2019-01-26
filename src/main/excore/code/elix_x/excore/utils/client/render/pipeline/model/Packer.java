package code.elix_x.excore.utils.client.render.pipeline.model;

import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excore.utils.client.render.model.UnpackedSimpleBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class Packer {

	public static PipelineElement<SimpleBakedModel, UnpackedSimpleBakedModel> unpack(){
		return PipelineElement.wrapper(UnpackedSimpleBakedModel::unpack);
	}

	public static PipelineElement<UnpackedSimpleBakedModel, SimpleBakedModel> pack(VertexFormat format){
		return PipelineElement.wrapper(model -> model.pack(format));
	}

}
