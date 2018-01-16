package appeng.core.me.parts.container;

import appeng.core.me.api.parts.container.IPartsContainer;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SmallDetachedPartAccess extends ContainerBasedPartAccess {

	protected Map<BlockPos, IPartsContainer> containers = new HashMap<>();

	@Override
	protected Optional<IPartsContainer> getContainer(BlockPos pos){
		return Optional.ofNullable(containers.get(pos));
	}

	@Override
	protected boolean canCreateContainer(BlockPos pos){
		return true;
	}

	@Override
	protected Optional<IPartsContainer> createContainer(BlockPos pos){
		IPartsContainer container = new PartsContainer();
		container.setGlobalAccess(this, null);
		container.setGlobalPosition(pos);
		containers.put(pos, container);
		return Optional.of(container);
	}

	@Override
	protected void removeContainer(BlockPos pos){
		containers.remove(pos);
	}
}
