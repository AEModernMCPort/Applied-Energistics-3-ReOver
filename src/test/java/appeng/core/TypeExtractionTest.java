package appeng.core;

import com.google.common.reflect.TypeToken;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TypeExtractionTest {

	@Test
	public void testGuavaTypeToken(){
		System.out.println(new TypeExtractor<String, Boolean>(){}.textract);
		System.out.println(new TypeExtractor<String, Boolean>(){}.iextract);
	}

	abstract class TypeExtractor<T, I> {
		TypeToken<T> textract = new TypeToken<T>(getClass()){};
		TypeToken<I> iextract = new TypeToken<I>(getClass()){};
	}

}