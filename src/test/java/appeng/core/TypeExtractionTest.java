package appeng.core;

import com.google.common.reflect.TypeToken;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TypeExtractionTest {

	@Test
	public void testGuavaTypeToken(){
		System.out.println(this.genericMethod());
	}

	public <T> Class genericMethod(){
		return new TypeExtractor<T>(){}.extract.getRawType();
	}

	abstract class TypeExtractor<T> {
		TypeToken<T> extract = new TypeToken<T>(getClass()){};
	}

}