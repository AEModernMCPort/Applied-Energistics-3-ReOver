package appeng.core.lib.util;

import javafx.util.Pair;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalUtil {

	public static <T> Optional<T> tryOrEmpty(Supplier<T> supplier){
		try {
			return Optional.ofNullable(supplier.get());
		} catch(Exception e){
			return Optional.empty();
		}
	}

}
