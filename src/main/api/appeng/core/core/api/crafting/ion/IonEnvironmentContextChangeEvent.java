package appeng.core.core.api.crafting.ion;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.Consumer;

public class IonEnvironmentContextChangeEvent extends Event {

	private final IonEnvironment environment;
	private final IonEnvironmentContext.Change change;
	private final Consumer productsConsumer;

	public IonEnvironmentContextChangeEvent(IonEnvironment environment, IonEnvironmentContext.Change change, Consumer productsConsumer){
		this.environment = environment;
		this.change = change;
		this.productsConsumer = productsConsumer;
	}

	public IonEnvironment getEnvironment(){
		return environment;
	}

	public IonEnvironmentContext.Change getChange(){
		return change;
	}

	public <T> void consume(T product){
		productsConsumer.accept(product);
	}

}
