package appeng.core.core.config;

import appeng.core.lib.config.ConfigLoader;
import code.elix_x.excomms.reflection.ReflectionHelper;
import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class YAMLConfigLoader<C> extends ConfigLoader<C> {

	public final YamlConfig CONFIG = new YamlConfig();

	public YAMLConfigLoader(String module){
		super(module, "yaml");
		CONFIG.setPrivateFields(true);
		CONFIG.writeConfig.setWriteDefaultValues(true);
		CONFIG.writeConfig.setWriteRootTags(false);
		CONFIG.setClassTag("feature", HierarchicalFeatures.class);
	}

	@Override
	public void load(Class<C> clas) throws IOException{
		super.load(clas);

		CONFIG.setClassTag("config", clas);

		YamlReader featuresReader = new YamlReader(new FileReader(featuresFile()), CONFIG);
		hierarchicalToManager(featuresReader.read(HierarchicalFeatures.class));
		featuresReader.close();

		YamlReader configReader = new YamlReader(new FileReader(configFile()), CONFIG);
		config = configReader.read(clas);
		configReader.close();
		if(config == null)
			config = new ReflectionHelper.AClass<>(clas).getDeclaredConstructor().setAccessible(true).newInstance();
	}

	@Override
	public void save() throws IOException{
		YamlWriter featuresWriter = new YamlWriter(new FileWriter(featuresFile()), CONFIG);
		featuresWriter.write(managerToHierarchical());
		featuresWriter.close();

		YamlWriter configWriter = new YamlWriter(new FileWriter(configFile()), CONFIG);
		configWriter.write(config);
		configWriter.close();
	}
}
