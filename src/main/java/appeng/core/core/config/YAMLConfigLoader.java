package appeng.core.core.config;

import appeng.api.config.ConfigCompilable;
import appeng.core.lib.config.ConfigLoader;
import code.elix_x.excomms.reflection.ReflectionHelper;
import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class YAMLConfigLoader<C> extends ConfigLoader<C> {

	public final YamlConfig FEATURESCONFIG = new YamlConfig();
	public final YamlConfig CONFIGCONFIG = new YamlConfig();

	public YAMLConfigLoader(String module, boolean dynamicDefaults){
		super(module, "yml");
		FEATURESCONFIG.writeConfig.setWriteRootTags(false);
		FEATURESCONFIG.setClassTag("feature", HierarchicalFeatures.class);
		//FIXME Write primitive types, or bad things happen (int -> string)
		CONFIGCONFIG.writeConfig.setWriteRootTags(false);
		CONFIGCONFIG.setPrivateFields(true);
		CONFIGCONFIG.writeConfig.setWriteDefaultValues(!dynamicDefaults);
		CONFIGCONFIG.readConfig.setIgnoreUnknownProperties(true);
	}

	@Override
	public void load(Class<C> clas) throws IOException{
		super.load(clas);

		YamlReader featuresReader = new YamlReader(new FileReader(featuresFile()), FEATURESCONFIG);
		hierarchicalToManager(featuresReader.read(HierarchicalFeatures.class));
		featuresReader.close();

		YamlReader configReader = new YamlReader(new FileReader(configFile()), CONFIGCONFIG);
		config = configReader.read(clas);
		configReader.close();
		if(config == null) config = new ReflectionHelper.AClass<>(clas).getDeclaredConstructor().setAccessible(true).newInstance();
		if(config instanceof ConfigCompilable) ((ConfigCompilable) config).compile();
	}

	@Override
	public void save() throws IOException{
		if(config instanceof ConfigCompilable) ((ConfigCompilable) config).decompile();

		YamlWriter featuresWriter = new YamlWriter(new FileWriter(featuresFile()), FEATURESCONFIG);
		featuresWriter.write(managerToHierarchical());
		featuresWriter.close();

		YamlWriter configWriter = new YamlWriter(new FileWriter(configFile()), CONFIGCONFIG);
		configWriter.write(config);
		configWriter.close();
	}
}
