package appeng.core.me.api.network.block;

import appeng.core.me.api.network.NetBlock;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Connection type for connecting devices inside a {@linkplain NetBlock network block}.
 *
 * @param <P> parameter type
 * @param <N> serialize parameter type
 *
 * @author Elix_x
 */
public interface Connection<P extends Comparable<P>, N extends NBTBase> {

	ResourceLocation getId();

	/**
	 * Join 2 connection parameters, aka calculate global connection parameter for 2 {@linkplain ConnectionPassthrough passthroughs}.
	 * @param param1 first parameter
	 * @param param2 second parameter
	 * @return global parameter
	 */
	@Nonnull
	P join(@Nonnull P param1, @Nonnull P param2);

	/**
	 * Add 2 parameters
	 * @param param1 first parameter
	 * @param param2 second parameter
	 * @return sum of 2 parameters
	 */
	@Nonnull
	P add(@Nonnull P param1, @Nonnull P param2);

	/**
	 * Subtract given consumption from the parameter
	 * @param param parameter
	 * @param sub value to subtract
	 * @return subtracted parameter
	 */
	@Nonnull
	P subtract(@Nonnull P param, @Nonnull P sub);

	/**
	 * Divides given parameter into equal parts by given number
	 * @param param parameter
	 * @param parts parts
	 * @return parameter of a part
	 */
	@Nonnull
	P divide(@Nonnull P param, int parts);

	/*
	 * IO
	 */

	N serializeParam(P param);

	P deserializeParam(N nbt);

}
