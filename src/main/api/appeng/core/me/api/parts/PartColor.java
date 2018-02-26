package appeng.core.me.api.parts;

import code.elix_x.excomms.color.RGBA;
import code.elix_x.excomms.reflection.ReflectionHelper;
import net.minecraftforge.common.util.EnumHelper;

import java.util.function.Predicate;

/**
 * Part color represents the color of parts to allow/disallow general connections between them based on their colors.<br>
 * This class is enum just for convenience, it is technically mutable. If you want to add a new color, use {@link #createNewColor(String, RGBA, Predicate)}. Do note though that if you want AE3 native parts to be created for your colors, you <b>must</b> create them before AE3 receives preInit <b>and</b> ship all required assets for native parts for new colors.
 *
 * @author Elix_x
 */
public enum PartColor {

	NOCOLOR(new RGBA(189, 169, 216), other -> true);

	public final RGBA refColor;
	private final Predicate<PartColor> compatibility;

	PartColor(RGBA refColor, Predicate<PartColor> compatibility){
		this.refColor = refColor;
		this.compatibility = compatibility;
	}

	public boolean canConnect(PartColor color){
		return compatibility.test(color) || color.compatibility.test(this);
	}

	/*
	 * Public mutability access
	 */

	private static final ReflectionHelper.AClass.AEnum<PartColor> ENUM = new ReflectionHelper.AClass<>(PartColor.class).asEnum();

	/**
	 * Creates a new part color.
	 * Note: if you want AE3 native parts to be created for your colors, you must create create them <b>before AE3 receives preInit</b>. Just remember to ship all necessary assets for native parts for new colors.
	 * @param name color name
	 * @param refColor reference color for default compatibility checks
	 * @param compatibility check verifying whether this color can be connected to given color
	 * @return created color
	 */
	public static PartColor createNewColor(String name, RGBA refColor, Predicate<PartColor> compatibility){
		//TODO Migrate back to EXComms once #advanced-reflective-operations is merged
//		return ENUM.addEnum(name, refColor, compatibility);
		return EnumHelper.addEnum(PartColor.class, name, new Class[]{RGBA.class, Predicate.class}, refColor, compatibility);
	}

	/**
	 * Creates a new part color.
	 * Note: if you want AE3 native parts to be created for your colors, you must create create them <b>before AE3 receives preInit</b>. Just remember to ship all necessary assets for native parts for new colors.
	 * @param name color name
	 * @param refColor reference color for default compatibility checks
	 * @return created color
	 */
	public static PartColor createNewColor(String name, RGBA refColor){
		return createNewColor(name, refColor, defaultCompatibility(refColor));
	}

	/**
	 * Default color-color compatibility check.
	 * Reminder: 2 colors are considered compatible when either one of them is compatible with the other
	 * @param refColor color to check against
	 * @return default compatibility check
	 */
	public static Predicate<PartColor> defaultCompatibility(RGBA refColor){
		return color -> color.refColor.equals(refColor);
	}

}
