package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.AppEngSkyfall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2d;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

public class Meteorite extends SkyobjectFalling<Meteorite, MeteoriteProvider> {

	protected double radius;

	public Meteorite(MeteoriteProvider provider){
		super(provider);
		physics = new SkyobjectFallingPhysics.WorldDriven(this, this.world, this.world.getBlockAccessBoundingBox()){

			@Override
			protected Map<BlockPos, IBlockState> getCollidingBlocks(World world){
				Map<BlockPos, IBlockState> affectedBlocks = new HashMap<>();
				StreamSupport.stream(BlockPos.getAllInBox(new BlockPos(pos.x - radius, pos.y - radius, pos.z - radius), new BlockPos(pos.x + radius, pos.y + radius, pos.z + radius)).spliterator(), false).filter(pos -> pos.distanceSqToCenter(this.pos.x, this.pos.y, this.pos.z) <= radius * radius).forEach(pos -> affectedBlocks.put(pos, world.getBlockState(pos)));
				return affectedBlocks;
			}

			@Override
			public NBTTagCompound serializeNBT(){
				NBTTagCompound nbt = super.serializeNBT();
				nbt.setDouble("radius", radius);
				return nbt;
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt){
				super.deserializeNBT(nbt);
				radius = nbt.getDouble("radius");
			}
		};
	}

	@Override
	protected Pair<Vec3d, Vec3d> calcSpawnPosMomentum(World world){
		Vec3d lading = landingPos(world);
		double startY = startY(world);
		Vector2d piTheta = piTheta(world);

		Vec3d pos = triangulateStartPos(lading, piTheta.x, piTheta.y, startY);
		Vec3d force = triangulateStartingForce(lading, piTheta.x, piTheta.y, startY, physics.getMass() * entrySpeed(world));

		return new ImmutablePair<>(pos, force);
	}

	protected Vec3d landingPos(World world){
		EntityPlayer player = world.playerEntities.get(0);
		return new Vec3d(player.posX, 128, player.posZ);
	}

	protected double startY(World world){
		return 300;
	}

	protected Vector2d piTheta(World world){
		return new Vector2d(-Math.PI + world.rand.nextDouble()*2*Math.PI, Math.toRadians(AppEngSkyfall.INSTANCE.config.meteorite.nextCreaseAngle(world.rand)));
	}

	protected double entrySpeed(World world){
		return 100;
	}

}
