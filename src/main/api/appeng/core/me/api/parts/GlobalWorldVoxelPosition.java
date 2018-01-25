package appeng.core.me.api.parts;

import appeng.api.pos.WorldReference;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public final class GlobalWorldVoxelPosition {

	private final WorldReference world;
	private final VoxelPosition position;

	public GlobalWorldVoxelPosition(WorldReference world, VoxelPosition position){
		this.world = world;
		this.position = position;
	}

	public WorldReference getWorld(){
		return world;
	}

	public VoxelPosition getPosition(){
		return position;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof GlobalWorldVoxelPosition)) return false;
		GlobalWorldVoxelPosition that = (GlobalWorldVoxelPosition) o;
		return Objects.equals(world, that.world) && Objects.equals(position, that.position);
	}

	@Override
	public int hashCode(){
		return Objects.hash(world, position);
	}

	@Override
	public String toString(){
		return "GlobalWorldVoxelPosition{" + "world=" + world + ", position=" + position + '}';
	}

}
