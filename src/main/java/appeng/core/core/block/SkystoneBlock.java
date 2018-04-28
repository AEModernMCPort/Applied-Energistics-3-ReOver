package appeng.core.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;

/**
 * @author dpeter99
 */
public class SkystoneBlock extends Block {

	protected final Variant variant;

	public SkystoneBlock(Variant variant){
		super(Material.ROCK);
		this.variant = variant;
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	public enum Variant implements IStringSerializable {
		STONE, BLOCK, BRICK, BRICK_SMALL;

		@Override
		public String getName(){
			return this.name().toLowerCase();
		}
	}

}
