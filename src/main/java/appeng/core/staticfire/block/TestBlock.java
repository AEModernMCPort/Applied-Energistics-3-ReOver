package appeng.core.staticfire.block;

import appeng.core.AppEng;
import appeng.core.staticfire.gui.StaticFireGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

// /setblock ~ ~ ~ appliedenergistics3:test
public class TestBlock extends Block{

    public TestBlock() {
        super(Material.ROCK);
        //setRegistryName("test");
    }

    /**
     * Called when the block is right clicked by a player.
     *
     * @param worldIn
     * @param pos
     * @param state
     * @param playerIn
     * @param hand
     * @param facing
     * @param hitX
     * @param hitY
     * @param hitZ
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        //System.out.print("asd");
        //if (!worldIn.isRemote) {
            playerIn.openGui(AppEng.instance(), 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            AppEng.logger.debug("asd2");
            System.out.print("asd");
        //}
        return false;
        //return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);

    }

}
