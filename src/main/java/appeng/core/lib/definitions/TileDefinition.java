package appeng.core.lib.definitions;


import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.ITileDefinition;


public final class TileDefinition<TE extends TileEntity> extends Definition<Class<TE>> implements ITileDefinition<TE>
{

	private final IBlockDefinition block;

	public TileDefinition( ResourceLocation identifier, Class<TE> tile, IBlockDefinition block )
	{
		super( identifier, tile );
		this.block = block;
	}

	@Override
	public <B extends Block> IBlockDefinition<B> block()
	{
		return block;
	}

	@Override
	public boolean isSameAs( Object other )
	{
		if( super.isSameAs( other ) )
		{
			return true;
		}
		else
		{
			if( isEnabled() )
			{
				Class<TE> clas = maybe().get();
				if( other instanceof TileEntity )
				{
					return other.getClass() == clas;
				}
				if( other instanceof Pair )
				{
					IBlockAccess world = (IBlockAccess) ( ( (Pair) other ).getLeft() instanceof IBlockAccess ? ( (Pair) other ).getLeft() : ( (Pair) other ).getRight() instanceof IBlockAccess ? ( (Pair) other ).getRight() : null );
					BlockPos pos = (BlockPos) ( ( (Pair) other ).getLeft() instanceof BlockPos ? ( (Pair) other ).getLeft() : ( (Pair) other ).getRight() instanceof BlockPos ? ( (Pair) other ).getRight() : null );
					if( world != null && pos != null )
					{
						return isSameAs( world.getTileEntity( pos ) );
					}
				}
			}
			return false;
		}
	}

}
