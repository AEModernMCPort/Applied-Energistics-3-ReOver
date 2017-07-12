package appeng.core.skyfall.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.api.definitions.ICoreItemDefinitions;
import appeng.core.core.client.bootstrap.ItemMeshDefinitionComponent;
import appeng.core.item.ItemMaterial;
import appeng.core.lib.definitions.Definitions;
import appeng.core.skyfall.api.definitions.ISkyfallItemDefinitions;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public class SkyfallItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements ISkyfallItemDefinitions {

	public SkyfallItemDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}
}
