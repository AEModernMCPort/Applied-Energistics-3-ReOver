package appeng.core.me.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IItemBuilder;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.core.client.bootstrap.ItemMeshDefinitionComponent;
import appeng.core.item.ItemMaterial;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.me.AppEngME;
import appeng.core.me.api.bootstrap.IPartBuilder;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import appeng.core.me.definition.PartDefinition;
import appeng.core.me.item.PartPlacerItem;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.Sys;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class PartDefinitionBuilder<P extends Part<P, S>, S extends Part.State<P, S>> extends DefinitionBuilder<P, P, IPartDefinition<P, S>, PartDefinitionBuilder<P, S>> implements IPartBuilder<P, S, PartDefinitionBuilder<P, S>> {

	private ResourceLocation mesh;
	private Function<IPartDefinition<P, S>, PartPlacementLogic> placementLogic;

	public PartDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, Part instance){
		super(factory, registryName, (P) instance, "part");
	}

	@Override
	public PartDefinitionBuilder<P, S> mesh(ResourceLocation mesh){
		this.mesh = mesh;
		return this;
	}

	@Override
	public PartDefinitionBuilder<P, S> createDefaultPlacerItem(Function<IPartDefinition<P, S>, PartPlacementLogic> placementLogic){
		this.placementLogic = placementLogic;
		return this;
	}

	@Override
	public PartDefinitionBuilder<P, S> createDefaultPlacerItem(){
		return createDefaultPlacerItem(def -> AppEngME.INSTANCE.createDefaultPlacementLogic(def.maybe().get()));
	}

	@Override
	protected IPartDefinition<P, S> def(@Nullable P part){
		if(part == null) return new PartDefinition<>(registryName, null);

		if(part.getUnlocalizedName() == null) part.setUnlocalizedName(registryName.getResourceDomain() + "." + registryName.getResourcePath());
		if(Loader.instance().activeModContainer().getModId().equals(AppEng.MODID)) mesh = new ResourceLocation(mesh != null ? mesh.getResourceDomain() : registryName.getResourceDomain(), AppEng.instance().getCurrentName() + "/" + (mesh != null ? mesh.getResourcePath() : (registryName.getResourcePath() + ".obj")));
		if(mesh != null) part.setMesh(mesh);

		PartDefinition<P, S> definition = new PartDefinition<>(registryName, part);
		if(placementLogic != null) factory.addDefault(factory.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(registryName, itemIh(new PartPlacerItem(placementLogic.apply(definition)))).initializationComponent(Side.CLIENT, new ItemMeshDefinitionComponent<Item>(() -> Optional.of(stack -> new ModelResourceLocation(part.getMesh(), "inventory")))).build());
		return definition;
	}

	private DefinitionFactory.InputHandler<Item, Item> itemIh(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
