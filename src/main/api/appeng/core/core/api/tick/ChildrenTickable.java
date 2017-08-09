package appeng.core.core.api.tick;

public interface ChildrenTickable<T> {

	void tick(T parent);

}
