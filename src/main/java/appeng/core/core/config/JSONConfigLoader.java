package appeng.core.core.config;

import appeng.api.config.ConfigCompilable;
import appeng.core.lib.config.ConfigLoader;
import code.elix_x.excomms.reflection.ReflectionHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

public class JSONConfigLoader<C> extends ConfigLoader<C> {

	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new TypeAdapter<ResourceLocation>() {

		@Override
		public void write(JsonWriter out, ResourceLocation value) throws IOException{
			out.value(value.toString());
		}

		@Override
		public ResourceLocation read(JsonReader in) throws IOException{
			return new ResourceLocation(in.nextString());
		}

	}).enableComplexMapKeySerialization().setPrettyPrinting().create();

	//TODO Implement dynamic defaults
	public JSONConfigLoader(String module, boolean dynamicDefaults){
		super(module, "json");
	}

	@Override
	public void load(Class<C> clas) throws IOException{
		super.load(clas);
		hierarchicalToManager(GSON.fromJson(new FileReader(featuresFile()), HierarchicalFeatures.class));
		config = GSON.fromJson(new FileReader(configFile()), clas);
		if(config == null) config = new ReflectionHelper.AClass<>(clas).getDeclaredConstructor().orElseThrow(configInstantiationFailed(clas, "no-args constructor not found")).setAccessible(true).newInstance().orElseThrow(configInstantiationFailed(clas, "constructor invocation failed"));
		if(config instanceof ConfigCompilable) ((ConfigCompilable) config).compile();
	}

	@Override
	public void save() throws IOException{
		if(config instanceof ConfigCompilable) ((ConfigCompilable) config).decompile();

		FileUtils.write(featuresFile(), GSON.toJson(managerToHierarchical()), Charset.defaultCharset());
		FileUtils.write(configFile(), GSON.toJson(config), Charset.defaultCharset());
	}

}
