package appeng.core.core.api.know;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SpecialKnowsCollectEvent<K> extends Event {

	private final K knowing;
	private final Map<String, INBTSerializable<NBTTagCompound>> specialKnow = new HashMap<>();

	public SpecialKnowsCollectEvent(K knowing){
		this.knowing = knowing;
	}

	public K getKnowing(){
		return knowing;
	}

	public <Kn extends INBTSerializable<NBTTagCompound>> void addSpeicalKnow(String skn, Kn sk){
		specialKnow.put(skn, sk);
	}

	public Map<String, INBTSerializable<NBTTagCompound>> getSKResult(){
		return Collections.unmodifiableMap(specialKnow);
	}

}
