package appeng.core.core.client.statemap;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class SubfolderStateMapper implements IStateMapper {

	private final IStateMapper delegate;
	private final String subfolder;

	public SubfolderStateMapper(IStateMapper delegate, String subfolder){
		this.delegate = delegate;
		this.subfolder = subfolder;
	}

	public SubfolderStateMapper(String subfolder){
		this(new DefaultStateMapper(), subfolder);
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn){
		return Maps.transformValues(delegate.putStateModelLocations(blockIn), model -> new ModelResourceLocation(new ResourceLocation(model.getResourceDomain(), String.join("/", subfolder, model.getResourcePath())), model.getVariant()));
	}
}
