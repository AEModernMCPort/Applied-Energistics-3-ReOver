package appeng.core.lib.items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class DroppingItemHandler implements IItemHandler {

	public World world;
	public Vec3d pos;

	public DroppingItemHandler(World world, Vec3d pos){
		this.world = world;
		this.pos = pos;
	}

	public DroppingItemHandler(World world, BlockPos pos){
		this(world, new Vec3d(pos).add(0.5, 0.5, 0.5));
	}

	@Override
	public int getSlots(){
		return 1;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot){
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
		world.spawnEntity(new EntityItem(world, pos.x, pos.y, pos.z, stack));
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate){
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot){
		return 64;
	}

}
