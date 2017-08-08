package appeng.core.core.material;

import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.IonProvider;
import appeng.core.core.api.item.IItemMaterial;
import appeng.core.core.api.material.Material;
import appeng.core.core.crafting.ion.IonProviderImpl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class IonMaterial extends Material implements ICapabilityProvider {

	@SubscribeEvent
	public static void attachItemCaps(AttachCapabilitiesEvent<ItemStack> event){
		if(event.getObject().getItem() instanceof IItemMaterial){
			Material material = ((IItemMaterial) event.getObject().getItem()).getMaterial(event.getObject());
			if(material instanceof IonMaterial) event.addCapability(new ResourceLocation(AppEng.MODID, "ion_provider"), (IonMaterial) material);
		}
	}

	protected IonProvider ionProvider;

	public IonMaterial(){

	}

	public IonProvider getIonProvider(){
		return ionProvider;
	}

	public void setIonProvider(IonProvider ionProvider){
		this.ionProvider = ionProvider;
	}

	public void findIonBySameRegName(int amount){
		setIonProvider(new IonProviderImpl(AppEngCore.INSTANCE.getIonRegistry().getValue(getRegistryName()), amount));
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
		return capability == AppEngCore.ionProviderCapability;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
		return capability == AppEngCore.ionProviderCapability ? (T) ionProvider : null;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem){
		AppEngCore.INSTANCE.getCraftingIonRegistry().onIonEntityItemTick(entityItem, ionProvider);
		return true;
	}
}
