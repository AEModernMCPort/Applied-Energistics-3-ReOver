package appeng.core.core.crafting.ion;

import appeng.core.core.api.crafting.ion.IonEnvironmentContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Optional;

public class IonEnvironmentContextImpl implements IonEnvironmentContext {

	protected final boolean isRemote;
	protected final Optional<World> world;
	protected final Optional<BlockPos> pos;
	protected final Optional<ICapabilityProvider> capabilities;

	public IonEnvironmentContextImpl(boolean isRemote, Optional<World> world, Optional<BlockPos> pos, Optional<ICapabilityProvider> capabilities){
		this.isRemote = isRemote;
		this.world = world;
		this.pos = pos;
		this.capabilities = capabilities;
	}

	public IonEnvironmentContextImpl(boolean isRemote, World world, BlockPos pos, ICapabilityProvider capabilities){
		this(isRemote, Optional.ofNullable(world), Optional.ofNullable(pos), Optional.ofNullable(capabilities));
	}

	public IonEnvironmentContextImpl(World world, BlockPos pos){
		this(world.isRemote, world, pos, null);
	}

	public IonEnvironmentContextImpl(boolean isRemote, ICapabilityProvider capabilities){
		this(isRemote, null, null, capabilities);
	}

	@Override
	public boolean isRemote(){
		return isRemote;
	}

	@Override
	public Optional<World> world(){
		return world;
	}

	@Override
	public Optional<BlockPos> pos(){
		return pos;
	}

	@Override
	public Optional<ICapabilityProvider> capabilities(){
		return capabilities;
	}
}
