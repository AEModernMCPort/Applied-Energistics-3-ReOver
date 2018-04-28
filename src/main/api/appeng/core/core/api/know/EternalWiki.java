package appeng.core.core.api.know;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;
import java.util.stream.Stream;

public interface EternalWiki extends IKnow {

	//Init

	void registerKnow(String know, Stream<String> parents);

	default void registerKnow(String know){
		registerKnow(know, Stream.empty());
	}

	//Util

	boolean isCompound(String know);

	default boolean isTerminal(String know){
		return !isCompound(know);
	}

	Stream<String> getParents(String know);

	Stream<String> getPieces(String know);

	default Stream<String> getAllPieces(String know){
		return Stream.concat(getPieces(know), getPieces(know).flatMap(this::getAllPieces));
	}

	//Impl

	@Override
	default boolean isAware(String know){
		return true;
	}

	@Override
	default boolean doesKnow(String know){
		return true;
	}

	@Override
	default void learn(String know){
		//You can't make the All-Knowing learn something new
	}

	@Override
	default void forget(String know){
		//You can't make the All-Knowing forget something
	}

	@Override
	default <K extends INBTSerializable<NBTTagCompound>> Optional<K> getSpecialKnow(String specialKnow){
		//Nothing special for the All-Knowing
		return Optional.empty();
	}

	@Override
	default NBTTagCompound serializeNBT(){
		//NOPE
		return new NBTTagCompound();
	}

	@Override
	default void deserializeNBT(NBTTagCompound nbt){
		//Nope
	}

}
