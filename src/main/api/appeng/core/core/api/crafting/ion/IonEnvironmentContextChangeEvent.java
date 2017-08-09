package appeng.core.core.api.crafting.ion;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.Consumer;

public class IonEnvironmentContextChangeEvent extends Event {

	private final IonEnvironment environment;
	private final Consumer productsConsumer;

	public IonEnvironmentContextChangeEvent(IonEnvironment environment, Consumer productsConsumer){
		this.environment = environment;
		this.productsConsumer = productsConsumer;
	}

	public IonEnvironment getEnvironment(){
		return environment;
	}

	public <T> void consume(T product){
		productsConsumer.accept(product);
	}

}
