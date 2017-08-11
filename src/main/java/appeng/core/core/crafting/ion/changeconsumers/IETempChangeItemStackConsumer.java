package appeng.core.core.crafting.ion.changeconsumers;

import appeng.core.core.api.crafting.ion.IonEnvironmentContext;
import appeng.core.core.api.crafting.ion.IonEnvironmentProductConsumer;
import appeng.core.lib.items.DroppingItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.function.Consumer;

public class IETempChangeItemStackConsumer implements IonEnvironmentProductConsumer<ItemStack> {

	@Override
	public Consumer<ItemStack> createConsumer(IonEnvironmentContext context){
		return stack -> context.capabilities().map(provider -> provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).orElse(new DroppingItemHandler(context.world().get(), context.pos().get())).insertItem(0, stack, false);
	}
}
