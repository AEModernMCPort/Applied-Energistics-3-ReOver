package appeng.api.definition;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@linkplain IDefinition}s are great. But what about features that can have <i>sub</i>types?<br>
 * We have created {@linkplain ISubDefinition} just for that! It extends capabilities of {@linkplain IDefinition} with properties to allow fluent sub-types integration with AE.
 *
 * @param <T> "Type of sub-type" - what this subdefinition holds
 * @param <P> Parent type - type of parent (normal) definition
 * @param <S> Type of this
 * @author Elix_x
 */
public interface ISubDefinition<T, P, S extends ISubDefinition<T, P, S>> extends IDefinition<T> {

	@Nullable
	ResourceLocation identifier();

	/**
	 * @return parent of this {@linkplain ISubDefinition}
	 */
	@Nonnull
	<PD extends IDefinition<P>> PD parent();

	/**
	 * Checks whether this subdefinition has given property.<br>
	 * <br>
	 * Note: All subdefinitions of the same definition have the same properties, but different values.
	 *
	 * @param property property to check
	 * @return whether this subdefinition has the property
	 */
	boolean hasProperty(ISubDefinitionProperty<?> property);

	/**
	 * Retrieves a property (of this sub-definition) by its' name.
	 *
	 * @param name name of the property
	 * @return {@linkplain ISubDefinitionProperty} with the given name, used by this {@linkplain ISubDefinition}
	 */
	@Nullable
	<V> ISubDefinitionProperty<V> getProperty(String name);

	/**
	 * Applies property to this sub definition to create a new sub definition.<br>
	 * <br>
	 * If this subdefinition does not have the property ({@linkplain #hasProperty(ISubDefinitionProperty)}) or the value is invalid ({@linkplain ISubDefinitionProperty#isValid(Object)}, this method does nothing.
	 *
	 * @param property property to change
	 * @param value    new value of
	 * @return new {@linkplain ISubDefinition} with modified property, or <tt>this</tt> if property and/or value is invalid.
	 */
	@Nonnull
	<V> S withProperty(ISubDefinitionProperty<V> property, V value);

	/**
	 * Name based version of {@link #withProperty(ISubDefinitionProperty, Object)}}.
	 *
	 * @param property name of property to change
	 * @param value    new value of
	 * @return new {@linkplain ISubDefinition} with modified property, or <tt>this</tt> if property and/or value is invalid.
	 */
	@Nonnull
	default <V> S withProperty(String property, V value){
		return withProperty(getProperty(property), value);
	}

	/**
	 * A property to alter {@linkplain ISubDefinition}s.
	 *
	 * @param <V> Value type
	 * @author Elix_x
	 */
	public interface ISubDefinitionProperty<V> {

		/**
		 * Gets the name of this property.
		 *
		 * @return name of this property
		 */
		@Nonnull
		String getName();

		/**
		 * Checks whether value is a valid value.
		 *
		 * @param value value to check
		 * @return Whether value is valid
		 */
		boolean isValid(V value);

	}

}
