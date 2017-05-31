package appeng.core.core.config;

import appeng.core.lib.config.ConfigLoader;
import code.elix_x.excomms.reflection.ReflectionHelper;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.FileReader;
import java.io.IOException;

public class JSONConfigLoader<C> extends ConfigLoader<C> {

	public static final Gson GSON = new GsonBuilder().addDeserializationExclusionStrategy(new ExclusionStrategy() {

		@Override
		public boolean shouldSkipField(FieldAttributes f){
			return f.getName().contains("___");
		}

		@Override
		public boolean shouldSkipClass(Class<?> clazz){
			return false;
		}

	}).create();

	public JSONConfigLoader(String module){
		super(module, "json");
	}

	@Override
	public void load(Class<C> clas) throws IOException{
		super.load(clas);
		hierarchicalToManager(GSON.fromJson(new FileReader(featuresFile()), HierarchicalFeatures.class));
		config = GSON.fromJson(new FileReader(configFile()), clas);
		if(config == null) config = new ReflectionHelper.AClass<>(clas).getDeclaredConstructor().setAccessible(true).newInstance();
	}

	@Override
	public void save() throws IOException{
		FileUtils.write(featuresFile(), GSON.toJson(managerToHierarchical()));
		FileUtils.write(configFile(), GSON.toJson(config));
	}

}
