package appeng.core.core.api.tick;

import java.util.function.Consumer;

public interface Tickables<T> extends Consumer<ChildrenTickable<T>> {

	void tick(T parent);

}
