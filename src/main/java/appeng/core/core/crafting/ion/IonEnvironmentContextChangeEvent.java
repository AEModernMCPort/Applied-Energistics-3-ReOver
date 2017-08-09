package appeng.core.core.crafting.ion;

import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.IonEnvironment;
import appeng.core.core.api.crafting.ion.IonEnvironmentContext;

public class IonEnvironmentContextChangeEvent extends appeng.core.core.api.crafting.ion.IonEnvironmentContextChangeEvent {

	public IonEnvironmentContextChangeEvent(IonEnvironment environment, IonEnvironmentContext context, IonEnvironmentContext.Change change){
		super(environment, change, AppEngCore.INSTANCE.getCraftingIonRegistry().compileProductConsumersC(context, change));
	}

}
