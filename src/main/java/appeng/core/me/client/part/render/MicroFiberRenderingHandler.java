package appeng.core.me.client.part.render;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.client.part.DefaultPartRenderingHandler;
import appeng.core.me.parts.part.connected.PartFiber;
import net.minecraft.util.EnumFacing;
import org.joml.Matrix4f;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.VOXELSIZEF2;

public class MicroFiberRenderingHandler extends DefaultPartRenderingHandler<PartFiber.Micro, PartFiber.MicroState> {

	@Override
	protected Matrix4f getTransforms(PartFiber.MicroState state, PartPositionRotation partPositionRotation){
		Matrix4f mat = super.getTransforms(state, partPositionRotation);
		EnumFacing line = state.getLine();
		if(line != null) mat = mat.translate(VOXELSIZEF2, VOXELSIZEF2, VOXELSIZEF2).rotate((float) Math.PI / 2, line.getDirectionVec().getY(), line.getDirectionVec().getX(), line.getDirectionVec().getZ()).translate(-VOXELSIZEF2, -VOXELSIZEF2, -VOXELSIZEF2);
		return mat;
	}

}
