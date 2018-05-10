package appeng.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Event marked with this annotation is a combination of multiple phases, aka pre, do and post - you can change which phase you're listening to with {@linkplain SubscribeEvent#priority()}.<br>
 * The default action happens in {@link EventPriority#NORMAL}, this means that to do your stuff before default action, subscribe with high priority, and if you want to do it after, subscribe with low priority. If the event is also marked as {@linkplain Cancelable}, cancelling it will stop further processing.
 *
 * @author Elix_x
 */
@Retention(value = RUNTIME)
@Target(value = TYPE)
public @interface Multiphased {}
