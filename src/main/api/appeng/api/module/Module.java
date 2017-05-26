
package appeng.api.module;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;


/**
 * Annotate module class with this annotation and make sure it has no-args constructor to add it to AE.
 * 
 * @author Elix_x
 *
 */
@Retention( RUNTIME )
@Target( TYPE )
public @interface Module
{
	/**
	 * 
	 * @return Name of the module
	 */
	String value();

	/**
	 * Used to define dependencies for your module. Syntax is generically similar to forge, with a few improvements.
	 * <br>
	 * You can leave this field empty, if you don't have any dependencies.
	 * <br>
	 * <br>
	 * Generic syntax {@link Pattern}:
	 * <code>(((client|server|after|before|hard|crash)-?)+(:(mod|module)-[^;]+)?)+</code>
	 * <br>
	 * <br>
	 * Each module dependency statement is seperated by a semicolon (;).
	 * To declare your module side-only, just use "client" or "server" as one dependency statement with "hard".
	 * Your module will not be loaded if used on the wrong side.
	 * Use "crash" if you want to stop game loading instead.
	 * <br>
	 * You can also declare dependency on a mod or AE2 module.
	 * For that, write your modifiers, seperated by a dash (-), followed by a colon (:), and the thing you want to depend on.
	 * You can depend on either a mod or a AE2 module.
	 * For mods use "mod-<modid>", repacing <modid> with the modid of the mod you want to depend on.
	 * For modules use "module-<name>", replacing <name> with the name of the module you want to depend on.
	 * <br>
	 * For modules dependency, you can declare loading order with modifiers. Not using any loading order modifier will result in random order.
	 * <br>
	 * <br>
	 * Modifiers:
	 * <ul>
	 * <li>"server"/"client" if you only depend on it on either client or server.
	 * <li>"after" will make your module load after the module specified here. Using "*" as module name will select all modules.
	 * <li>"before" will make you module load before the module specified here. Using "*" as module name will select all modules.
	 * <li>"hard" will make that dependency hard, aka your module will load if and only if it is there.
	 * <li>"crash" will make the game crash if the dependency is not there instead of just not loading your module. Use with "hard".
	 * </ul>
	 * 
	 * @return module's dependencies
	 * 
	 * @author Elix_x
	 */
	String dependencies() default "";

	/**
	 * Populate given field with instance of module with given name or class. Works similarly to {@link Mod#Instance}, but for modules.
	 * <br>
	 * Field <b>must be static</b> unless it is located inside module class.
	 * Works with private and/or final fields.
	 * 
	 * @author Elix_x
	 */
	@Retention( RetentionPolicy.RUNTIME )
	@Target( ElementType.FIELD )
	public @interface Instance
	{
		/**
		 * @return Name or class of module to inject it's instance.
		 */
		String value();

	}

	/**
	 * Marks the associated method as handling an FML lifecycle event redirected from AE.
	 * For more details and list of events, see {@link EventHandler}.
	 * 
	 * @author Elix_x
	 *
	 */
	@Retention( RetentionPolicy.RUNTIME )
	@Target( ElementType.METHOD )
	public @interface ModuleEventHandler
	{

	}

}
