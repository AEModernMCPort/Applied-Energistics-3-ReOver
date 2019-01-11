package appeng.core.me.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IItemBuilder;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.core.client.bootstrap.ItemMeshDefinitionComponent;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.me.AppEngME;
import appeng.core.me.api.bootstrap.IPartBuilder;
import appeng.core.me.api.client.part.PartRenderingHandler;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import appeng.core.me.definition.PartDefinition;
import appeng.core.me.item.PartPlacerItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class PartDefinitionBuilder<P extends Part<P, S>, S extends Part.State<P, S>> extends DefinitionBuilder<P, P, IPartDefinition<P, S>, PartDefinitionBuilder<P, S>> implements IPartBuilder<P, S, PartDefinitionBuilder<P, S>> {

	private ResourceLocation mesh;
	private Function<IPartDefinition<P, S>, PartPlacementLogic> placementLogic;
	private Supplier<Optional<PartRenderingHandler<P, S>>> renderingHandler = () -> Optional.empty();

	public PartDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, Part instance){
		super(factory, registryName, (P) instance, "part");
	}

	@Override
	public PartDefinitionBuilder<P, S> rootMesh(ResourceLocation mesh){
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
		return createDefaultPlacerItem(def -> AppEngME.INSTANCE.getPartsHelper().createDefaultPlacementLogic(def.maybe().get()));
	}

	@Override
	public PartDefinitionBuilder<P, S> overrideRenderingHandler(Supplier<Optional<PartRenderingHandler<P, S>>> renderingHandler){
		this.renderingHandler = renderingHandler;
		return this;
	}

	@Override
	protected IPartDefinition<P, S> def(@Nullable P part){
		if(part == null) return new PartDefinition<>(registryName, null);

		if(part.getUnlocalizedName() == null) part.setUnlocalizedName(registryName.getNamespace() + "." + registryName.getPath());
		if(Loader.instance().activeModContainer().getModId().equals(AppEng.MODID)) mesh = new ResourceLocation(mesh != null ? mesh.getNamespace() : registryName.getNamespace(), AppEng.instance().getCurrentName() + "/" + (mesh != null ? mesh.getPath() : (registryName.getPath() + ".obj")));
		if(mesh != null) part.setRootMesh(mesh);

		PartDefinition<P, S> definition = new PartDefinition<>(registryName, part);
		if(placementLogic != null) factory.addDefault(factory.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(registryName, itemIh(new PartPlacerItem(placementLogic.apply(definition)))).initializationComponent(Side.CLIENT, new ItemMeshDefinitionComponent<Item>(() -> Optional.of(stack -> new ModelResourceLocation(part.getRootMesh(), "inventory")))).build());
		this.<DefinitionInitializationComponent.PreInit<P, IPartDefinition<P, S>>>initializationComponent(Side.CLIENT, def -> AppEngME.proxy.clientPartHelper().ifPresent(clientPartHelper -> clientPartHelper.setRenderingHandler(def.maybe().get(), renderingHandler.get())));
		return definition;
	}

	private DefinitionFactory.InputHandler<Item, Item> itemIh(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
