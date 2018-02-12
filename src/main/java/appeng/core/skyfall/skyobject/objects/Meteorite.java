package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.AppEngSkyfall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Meteorite extends SkyobjectFalling<Meteorite, MeteoriteProvider> {

	protected double radius;

	public Meteorite(MeteoriteProvider provider){
		super(provider);
		physics = new SkyobjectFallingPhysics.WorldDriven(this, this.world, this.world.getBlockAccessBoundingBox()){

			private Stream<BlockPos> colldingBlocks(World world, Vector3d pos, double radius){
				return StreamSupport.stream(BlockPos.getAllInBox(new BlockPos(pos.x - radius, pos.y - radius, pos.z - radius), new BlockPos(pos.x + radius, pos.y + radius, pos.z + radius)).spliterator(), false).filter(blockPos -> blockPos.distanceSqToCenter(this.pos.x, this.pos.y, this.pos.z) <= radius * radius).filter(canBlockBeAffected(world));
			}

			@Override
			protected Map<BlockPos, IBlockState> getCollidingBlocks(World world){
				Map<BlockPos, IBlockState> affectedBlocks = new HashMap<>();
				colldingBlocks(world, new Vector3d(pos.x, pos.y, pos.z), radius*1.5).forEach(pos -> affectedBlocks.put(pos, world.getBlockState(pos)));
				return affectedBlocks;
			}

			@Override
			protected void affectBlocks(World world, Map<BlockPos, IBlockState> affectedBlocks, Vector3d totalReaction){
				super.affectBlocks(world, affectedBlocks, totalReaction);

				Vector3d oDir = new Vector3d(momentum.x, momentum.y, momentum.z).mul(-1).normalize();
				Stream<BlockPos> affected = Stream.empty();
				for(double rf = 2; rf < 3; rf += 0.25) affected = Stream.concat(affected, colldingBlocks(world, new Vector3d(pos.x, pos.y, pos.z).add(new Vector3d(oDir).mul((rf+0.5) * radius)), rf * radius));

				Map<BlockPos, IBlockState> reflectedBlocks = new HashMap<>();
				affected.forEach(pos -> reflectedBlocks.put(pos, world.getBlockState(pos)));
				super.affectBlocks(world, reflectedBlocks, totalReaction);
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
	protected Vec3d[] initialConditions(World world){
		Vec3d lading = landingPos(world);
		double startY = startY(world);
		Vector2d piTheta = piTheta(world);

		Vec3d pos = triangulateStartPos(lading, piTheta.x, piTheta.y, startY);
		Vec3d force = triangulateStartingForce(lading, piTheta.x, piTheta.y, startY, physics.getMass() * entrySpeed(world));
		return new Vec3d[]{pos, force, Vec3d.ZERO, Vec3d.ZERO};
	}

	protected Vec3d landingPos(World world){
		EntityPlayer player = world.playerEntities.get(0);
		return new Vec3d(player.posX, 128, player.posZ);
	}

	protected double startY(World world){
		return 2500;
	}

	protected Vector2d piTheta(World world){
		return new Vector2d(-Math.PI + world.rand.nextDouble()*2*Math.PI, Math.toRadians(AppEngSkyfall.INSTANCE.config.meteorite.nextCreaseAngle(world.rand)));
	}

	protected double entrySpeed(World world){
		return 1000;
	}

}
