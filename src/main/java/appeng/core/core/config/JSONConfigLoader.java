package appeng.core.core.config;

import appeng.core.lib.config.ConfigLoader;
import code.elix_x.excomms.reflection.ReflectionHelper;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

	}).registerTypeAdapter(Multimap.class, new JsonSerializer<Multimap>() {

		public JsonElement serialize(Multimap multimap, Type type, JsonSerializationContext jsonSerializationContext){
			return jsonSerializationContext.serialize(multimap.asMap());
		}

	}).registerTypeAdapter(Multimap.class, new JsonDeserializer<Multimap>() {

		public Multimap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
				throws JsonParseException{
			final SetMultimap<String, String> map = Multimaps.<String, String>newSetMultimap(new HashMap<String, Collection<String>>(), new com.google.common.base.Supplier<Set<String>>() {

				public Set<String> get(){
					return Sets.newHashSet();
				}
			});
			for(Map.Entry<String, JsonElement> entry : ((JsonObject) jsonElement).entrySet()){
				for(JsonElement element : (JsonArray) entry.getValue()){
					map.get(entry.getKey()).add(element.getAsString());
				}
			}
			return map;
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
