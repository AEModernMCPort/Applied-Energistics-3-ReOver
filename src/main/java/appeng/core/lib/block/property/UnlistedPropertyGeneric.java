package appeng.core.lib.block.property;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyGeneric<V> implements IUnlistedProperty<V> {

	public static final UnlistedPropertyGeneric<World> WORLD = new UnlistedPropertyGeneric<>("world", World.class);
	public static final UnlistedPropertyGeneric<IBlockAccess> BLOCKACESS = new UnlistedPropertyGeneric<>("world", IBlockAccess.class);
	public static final UnlistedPropertyGeneric<BlockPos> POS = new UnlistedPropertyGeneric<>("pos", BlockPos.class);
	public static final UnlistedPropertyGeneric<TileEntity> TILE = new UnlistedPropertyGeneric<>("tile", TileEntity.class);

	protected String name;
	protected Class<V> type;

	public UnlistedPropertyGeneric(String name, Class<V> type){
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public boolean isValid(V value){
		return true;
	}

	@Override
	public Class<V> getType(){
		return type;
	}

	@Override
	public String valueToString(V value){
		return value.toString();
	}
}
