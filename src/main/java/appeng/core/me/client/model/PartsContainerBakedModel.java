package appeng.core.me.client.model;

import appeng.core.lib.block.property.UnlistedPropertyGeneric;
import appeng.core.me.AppEngME;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PartsContainerBakedModel extends SimpleBakedModel {

	public PartsContainerBakedModel(){
		super(null, null, false, false, ModelLoader.White.INSTANCE, ItemCameraTransforms.DEFAULT, ItemOverrideList.NONE);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState sstate, @Nullable EnumFacing side, long rand){
		return side == null ? Optional.of(sstate).map(state -> (IExtendedBlockState) state).map(state -> state.getValue(UnlistedPropertyGeneric.BLOCKACESS).getTileEntity(state.getValue(UnlistedPropertyGeneric.POS))).map(tile -> tile.getCapability(PartsHelper.partsContainerCapability, null)).map(AppEngME.proxy.clientPartHelper().get()::bakeQuads).orElse(Collections.EMPTY_LIST) : Collections.EMPTY_LIST;
	}
}
