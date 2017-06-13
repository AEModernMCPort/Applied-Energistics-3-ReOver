package appeng.core.lib.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.apache.commons.lang3.tuple.Pair;

public final class TileDefinition<TE extends TileEntity> extends Definition<TileRegistryEntry<TE>> implements ITileDefinition<TE> {

	private IBlockDefinition block;

	public TileDefinition(ResourceLocation identifier, TileRegistryEntry<TE> tile){
		super(identifier, tile);
	}

	public void setBlock(IBlockDefinition block){
		this.block = block;
	}

	@Override
	public <B extends Block & ITileEntityProvider> IBlockDefinition<B> block(){
		return block;
	}

	@Override
	public boolean isSameAs(Object other){
		if(super.isSameAs(other)){
			return true;
		} else {
			if(isEnabled()){
				Class<TE> clas = maybe().get().getTileClass();
				if(other instanceof TileEntity){
					return other.getClass() == clas;
				}
				if(other instanceof Pair){
					IBlockAccess world = (IBlockAccess) (((Pair) other).getLeft() instanceof IBlockAccess ? ((Pair) other).getLeft() : ((Pair) other).getRight() instanceof IBlockAccess ? ((Pair) other).getRight() : null);
					BlockPos pos = (BlockPos) (((Pair) other).getLeft() instanceof BlockPos ? ((Pair) other).getLeft() : ((Pair) other).getRight() instanceof BlockPos ? ((Pair) other).getRight() : null);
					if(world != null && pos != null){
						return isSameAs(world.getTileEntity(pos));
					}
				}
			}
			return false;
		}
	}

}
