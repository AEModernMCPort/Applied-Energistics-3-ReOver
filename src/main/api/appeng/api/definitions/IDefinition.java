
package appeng.api.definitions;


import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.sub.ISubDefinition;


/**
 * Definition is an interface wrapper holding any feature created by AE.<br>
 * AE's configs allow end user to disable/enable absolutely any small or big part of features. So we needed a solution that allows to work with features without knowing at compile time whether it is enabled or not.<br>
 * Meet {{@linkplain IDefinition}} - an interface warpping features and using power of {@linkplain Optional} to make sure that everything works smoothly in all possible cases.
 * 
 * @author Elix_x
 *
 * @param <T> What this definition defines
 */
public interface IDefinition<T>
{

	/**
	 * @return the unique name of the definition which will be used to register the underlying structure.
	 */
	@Nonnull
	ResourceLocation identifier();

	/**
	 * @return Implementation if applicable
	 */
	<R extends T> Optional<R> maybe();

	/**
	 * @param <D> Sub-definition type
	 * @param <P> Parent type
	 * @param <S> Sub-definition definition type
	 * 
	 * @return {@linkplain ISubDefinition} representation if applicable
	 */
	<D, P extends T, S extends ISubDefinition<D, P, S>> Optional<S> maybeSubDefinition();

	/**
	 * @return <tt>true</tt> if definition is enabled
	 */
	boolean isEnabled();

	/**
	 * Compare other with implementation.
	 * <br>
	 * Implemented in a smart way - for example - Comparing {@linkplain ItemStack} (<tt>other</tt>) to Item (<tt>this</tt>) will compare actual {@linkplain ItemStack#getItem()} and <tt>this</tt>.
	 * <br>
	 * Supports {@linkplain Pair}s and {@linkplain Triple}s when applicable.
	 * 
	 * @param other compared object
	 * @return whether the objects are the same
	 */
	boolean isSameAs( Object other );

}
