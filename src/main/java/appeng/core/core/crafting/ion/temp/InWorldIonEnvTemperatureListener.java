package appeng.core.core.crafting.ion.temp;

import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.IonEnvironmentContext;
import appeng.core.core.api.crafting.ion.NativeEnvironmentChange;
import appeng.core.core.api.tick.ChildrenTickable;
import appeng.core.core.api.tick.Tickables;
import appeng.core.core.crafting.ion.CraftingIonRegistry;
import appeng.core.core.crafting.ion.IonEnvironmentContextChangeEvent;
import appeng.core.core.crafting.ion.IonEnvironmentContextImpl;
import appeng.core.core.tile.IonEnvironmentTile;
import appeng.core.lib.capability.SingleCapabilityProvider;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class InWorldIonEnvTemperatureListener implements ChildrenTickable<TileEntity>, INBTSerializable<NBTTagInt> {

	@SubscribeEvent
	public static void attachTempListener(AttachCapabilitiesEvent<TileEntity> event){
		if(event.getObject() instanceof IonEnvironmentTile){
			InWorldIonEnvTemperatureListener temperatureListener = new InWorldIonEnvTemperatureListener();
			event.addCapability(new ResourceLocation(AppEng.MODID, "env_temp_listener"), new SingleCapabilityProvider<>(CraftingIonRegistry.ionEnvTemperatureListenerCapability, temperatureListener));
			Tickables.getPendingTickablesProvider(event).getCapability(AppEngCore.tickablesCapability, null).accept(temperatureListener);
		}
	}

	public static final double RANGED = 2.5;
	public static final double RANGEI = 3;

	public static Map<Block, Integer> temperatures = new HashMap<>();

	static {
		//TODO Unhardcode
		temperatures.put(Blocks.FIRE, 1);
		temperatures.put(Blocks.NETHERRACK, 2);
		temperatures.put(Blocks.LAVA, 3);
		temperatures.put(Blocks.FLOWING_LAVA, 3);

		temperatures.put(Blocks.SNOW, 1);
		temperatures.put(Blocks.ICE, 2);
		temperatures.put(Blocks.PACKED_ICE, 3);
	}

	public static int computeDelta(World world, BlockPos pos){
		//All Blocks in range of 2.5 blocks
		MutableInt temp = new MutableInt(0);
		StreamSupport.stream(BlockPos.getAllInBox(pos.add(-RANGEI, -RANGEI, -RANGEI), pos.add(RANGEI, RANGEI, RANGEI)).spliterator(), false).filter(npos -> npos.distanceSq(pos) <= RANGED * RANGED).forEach(npos -> Optional.ofNullable(temperatures.get(world.getBlockState(npos).getBlock())).ifPresent(temp::add));
		return temp.getValue();
	}

	public static final int COOLINGTHRESHOLD = -50;
	public static final int HEATINGTHRESHOLD = +50;

	public static void onChange(TileEntity tile, IonEnvironmentContext.Change change){
		MinecraftForge.EVENT_BUS.post(new IonEnvironmentContextChangeEvent(tile.getCapability(CraftingIonRegistry.ionEnvironmentCapability, null), new IonEnvironmentContextImpl(tile.getWorld(), tile.getPos()), change));
	}

	public int temp;

	@Override
	public void tick(TileEntity parent){
		temp += computeDelta(parent.getWorld(), parent.getPos());
		if(temp >= HEATINGTHRESHOLD) onChange(parent, NativeEnvironmentChange.HEATING);
		if(temp <= COOLINGTHRESHOLD) onChange(parent, NativeEnvironmentChange.COOLING);
	}

	@Override
	public NBTTagInt serializeNBT(){
		return new NBTTagInt(temp);
	}

	@Override
	public void deserializeNBT(NBTTagInt nbt){
		temp = nbt.getInt();
	}
}
