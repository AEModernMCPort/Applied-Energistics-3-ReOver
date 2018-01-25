package appeng.core.me.api.network;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface W2NInterface {

	@Nonnull
	Optional<Network> getNetwork(@Nonnull NetworkUUID uuid);

}
