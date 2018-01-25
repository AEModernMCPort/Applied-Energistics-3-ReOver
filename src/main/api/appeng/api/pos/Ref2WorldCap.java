package appeng.api.pos;

import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface Ref2WorldCap {

	@Nonnull Optional<World> getWorld(@Nonnull WorldReference reference);

	@Nonnull WorldReference getReference(@Nonnull World world);

}
