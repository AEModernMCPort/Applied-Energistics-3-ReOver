package appeng.core.core.definition;

import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.lib.definition.Definition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public class BlockDefinition<B extends Block> extends Definition<B> implements IBlockDefinition<B> {

	private IItemDefinition<? extends ItemBlock> item;

	public BlockDefinition(ResourceLocation identifier, B block){
		super(identifier, block);
	}

	public <I extends ItemBlock> void setItem(IItemDefinition<I> item){
		this.item = item;
	}

	@Override
	public <I extends ItemBlock> Optional<IItemDefinition<I>> maybeItem(){
		return Optional.ofNullable((IItemDefinition<I>) item);
	}

	@Override
	public boolean isSameAs(Object other){
		if(super.isSameAs(other)){
			return true;
		} else {
			if(isEnabled()){
				Block block = maybe().get();
				if(other instanceof IBlockState){
					return ((IBlockState) other).getBlock() == block;
				}
				if(other instanceof Pair){
					IBlockAccess world = (IBlockAccess) (((Pair) other).getLeft() instanceof IBlockAccess ? ((Pair) other).getLeft() : ((Pair) other).getRight() instanceof IBlockAccess ? ((Pair) other).getRight() : null);
					BlockPos pos = (BlockPos) (((Pair) other).getLeft() instanceof BlockPos ? ((Pair) other).getLeft() : ((Pair) other).getRight() instanceof BlockPos ? ((Pair) other).getRight() : null);
					if(world != null && pos != null){
						return isSameAs(world.getBlockState(pos));
					}
				}
			}
			return false;
		}
	}
}
