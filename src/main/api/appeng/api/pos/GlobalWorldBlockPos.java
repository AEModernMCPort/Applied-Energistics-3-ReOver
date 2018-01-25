package appeng.api.pos;

import net.minecraft.util.math.BlockPos;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public final class GlobalWorldBlockPos {

	private final WorldReference world;
	private final BlockPos pos;

	public GlobalWorldBlockPos(WorldReference world, BlockPos pos){
		this.world = world;
		this.pos = pos;
	}

	public WorldReference getWorld(){
		return world;
	}

	public BlockPos getPos(){
		return pos;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof GlobalWorldBlockPos)) return false;
		GlobalWorldBlockPos that = (GlobalWorldBlockPos) o;
		return Objects.equals(world, that.world) && Objects.equals(pos, that.pos);
	}

	@Override
	public int hashCode(){
		return Objects.hash(world, pos);
	}

	@Override
	public String toString(){
		return "GlobalWorldBlockPos{" + "world=" + world + ", pos=" + pos + '}';
	}

}
