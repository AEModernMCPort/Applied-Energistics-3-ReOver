package appeng.core.staticfire.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

/**
 * Created by Peter on 2017. 06. 28..
 */
public class SkyBlock  extends Block implements StaticFireBlockBase{

    final String REGISTRY_NAME = "sky_stone";

    public static final IProperty<Variant> variant = PropertyEnum.create("variant",Variant.class);

    public SkyBlock() {
        super(Material.ROCK);
    }

    @Override
    public String getRegistryNameSF() {
        return REGISTRY_NAME;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        for (Variant variants : Variant.values())
        {
            items.add(new ItemStack(this, 1, variants.ordinal()));
        }
    }


    /**
     * Convert the given metadata into a BlockState for this Block
     *
     * @param meta
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(variant,Variant.values()[meta]);
    }

    /**
     * Convert the BlockState into the correct metadata value
     *
     * @param state
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(variant).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,variant);
    }

    public enum Variant implements IStringSerializable{
        STONE,
        BLOCK,
        BRICK,
        SMALLBRICK;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }
    }


}
