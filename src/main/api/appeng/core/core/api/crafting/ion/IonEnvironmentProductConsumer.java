package appeng.core.core.api.crafting.ion;

import java.util.function.Consumer;

public interface IonEnvironmentProductConsumer<T> {

	Consumer<T> createConsumer(IonEnvironmentContext context);

}
