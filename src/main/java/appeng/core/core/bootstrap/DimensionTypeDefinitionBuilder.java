package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IDimensionTypeDefinition;
import appeng.core.core.api.bootstrap.IDimensionTypeBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.core.definition.DimensionTypeDefinition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

public class DimensionTypeDefinitionBuilder<D extends DimensionType> extends DefinitionBuilder<Integer, D, IDimensionTypeDefinition<D>, DimensionTypeDefinitionBuilder<D>> implements IDimensionTypeBuilder<D, DimensionTypeDefinitionBuilder<D>> {

	private String name;
	private String suffix;
	private Class<? extends WorldProvider> clazz;
	private boolean shouldLoadSpawn;

	public DimensionTypeDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, int id){
		super(factory, registryName, id, "dimension");
		this.name = registryName.toString();
		this.suffix = registryName.getResourcePath();
	}

	public DimensionTypeDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, int id, String name, String suffix, Class<? extends WorldProvider> clazz, boolean shouldLoadSpawn){
		super(factory, registryName, id, "dimension");
		this.name = name;
		this.suffix = suffix;
		this.clazz = clazz;
		this.shouldLoadSpawn = shouldLoadSpawn;
	}

	public DimensionTypeDefinitionBuilder<D> name(String name){
		this.name = name;
		return this;
	}

	public DimensionTypeDefinitionBuilder<D> suffix(String suffix){
		this.suffix = suffix;
		return this;
	}

	public DimensionTypeDefinitionBuilder<D> clazz(Class<? extends WorldProvider> clazz){
		this.clazz = clazz;
		return this;
	}

	public DimensionTypeDefinitionBuilder<D> shouldLoadSpawn(boolean shouldLoadSpawn){
		this.shouldLoadSpawn = shouldLoadSpawn;
		return this;
	}

	@Override
	public IDimensionTypeDefinition<D> def(Integer id){
		if(id == null) return new DimensionTypeDefinition<>(registryName, null);

		return new DimensionTypeDefinition<>(registryName, (D) DimensionType.register(name, suffix, id, clazz, shouldLoadSpawn));
	}

}
