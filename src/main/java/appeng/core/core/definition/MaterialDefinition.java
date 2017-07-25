package appeng.core.core.definition;

import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.core.api.definition.IItemSubDefinition;
import appeng.api.item.IStateItemState;
import appeng.core.core.api.item.IItemMaterial;
import appeng.core.core.api.material.Material;
import appeng.core.core.AppEngCore;
import appeng.core.core.definitions.CoreItemDefinitions;
import appeng.core.lib.definition.Definition;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class MaterialDefinition<M extends Material> extends Definition<M> implements IMaterialDefinition<M> {

	private static IItemDefinition itemMaterialDefinition;

	private static <I extends Item & IItemMaterial<I>> IItemDefinition<I> itemMaterialDefinition(){
		if(itemMaterialDefinition != null){
			return itemMaterialDefinition;
		}
		if(AppEngCore.INSTANCE.<Item, CoreItemDefinitions>definitions(Item.class) != null && AppEngCore.INSTANCE.<Item, CoreItemDefinitions>definitions(Item.class).material() != null){
			return itemMaterialDefinition = AppEngCore.INSTANCE.<Item, CoreItemDefinitions>definitions(Item.class).getUncheked("material");
		}
		return null;
	}

	public MaterialDefinition(ResourceLocation identifier, M material){
		super(identifier, material);
	}

	@Override
	public boolean isSameAs(Object other){
		// TODO 1.11.2-CD:A - Add checks
		return super.isSameAs(other);
	}

	@Override
	public <S extends IStateItemState<I>, I extends Item & IItemMaterial<I>, D extends IItemSubDefinition<S, I>> Optional<D> maybeAsSubDefinition(){
		return (Optional<D>) maybe().map(material -> MaterialDefinition.<I>itemMaterialDefinition().<S, I, IItemSubDefinition<S, I>>maybeSubDefinition().get().withProperty("material", null));
	}

}
