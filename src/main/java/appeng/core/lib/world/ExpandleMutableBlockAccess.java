package appeng.core.lib.world;

import code.elix_x.excore.utils.world.MutableBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class ExpandleMutableBlockAccess implements MutableBlockAccess, INBTSerializable<NBTTagCompound> {

	protected final Vec3i chunkSize;
	protected Vec3i worldSize;

	protected boolean dirty;

	protected IBlockState defaultState = Blocks.AIR.getDefaultState();
	protected Biome biome = Biomes.PLAINS;
	protected WorldType worldType = WorldType.DEFAULT;
	protected Function<EnumSkyBlock, Integer> light = enumSkyBlock -> enumSkyBlock == EnumSkyBlock.SKY ? 15 : 0;

	protected Map<BlockPos, Chunk> chunks = new HashMap<>();

	public ExpandleMutableBlockAccess(Vec3i chunkSize){
		this.chunkSize = chunkSize;
	}

	public ExpandleMutableBlockAccess(){
		this(new Vec3i(16, 16, 16));
	}

	/*
	 * Pos transforms
	 */

	public BlockPos toChunkPos(BlockPos blockPos){
		return new BlockPos(Math.floorDiv(blockPos.getX(), chunkSize.getX()), Math.floorDiv(blockPos.getY(), chunkSize.getY()), Math.floorDiv(blockPos.getZ(), chunkSize.getZ()));
	}

	public BlockPos toChunkOriginBlockPos(BlockPos chunkPos){
		return new BlockPos(chunkPos.getX() * chunkSize.getX(), chunkPos.getY() * chunkSize.getY(), chunkPos.getZ() * chunkSize.getZ());
	}

	/*
	 * Dirty
	 */

	public boolean isDirty(){
		return chunks.values().stream().anyMatch(Chunk::isDirty);
	}

	/*
	 * Chunk access
	 */

	public boolean chunkExists(BlockPos chunkPos){
		return chunks.containsKey(chunkPos);
	}

	public Chunk getChunk(BlockPos chunkPos){
		return chunks.get(chunkPos);
	}

	protected Chunk createNewChunk(BlockPos chunkPos){
		return new Chunk(chunkPos);
	}

	public Chunk getOrCreateChunk(BlockPos chunkPos){
		Chunk chunk = getChunk(chunkPos);
		if(chunk == null) chunks.put(chunkPos, chunk = createNewChunk(chunkPos));
		return chunk;
	}

	public Stream<Chunk> getAllChunks(){
		return chunks.values().stream();
	}

	/*
	 * Util
	 */

	public AxisAlignedBB getBlockAccessBoundingBox(){
		return chunks.values().stream().findAny().map(chunk -> {
			MutableObject<AxisAlignedBB> box = new MutableObject<>(chunk.getBoundingBox());
			chunks.values().forEach(next -> box.setValue(box.getValue().union(next.getBoundingBox())));
			return box.getValue();
		}).orElse(new AxisAlignedBB(0, 0, 0, 0, 0, 0));
	}

	public Stream<Chunk> getDirtyChunks(){
		return getAllChunks().filter(Chunk::isDirty);
	}

	/*
	 * Implementation
	 */

	@Override
	public IBlockState getBlockState(BlockPos pos){
		return Optional.ofNullable(getChunk(toChunkPos(pos))).map(chunk -> chunk.getBlockStateG(pos)).orElse(defaultState);
	}

	@Override
	public void setBlockState(BlockPos pos, IBlockState state){
		getOrCreateChunk(toChunkPos(pos)).setBlockStateG(pos, state);
	}

	public void setDefaultState(IBlockState defaultState){
		this.defaultState = defaultState;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos){
		return Optional.ofNullable(getChunk(toChunkPos(pos))).map(chunk -> chunk.getTileEntityG(pos)).orElse(null);
	}

	@Override
	public void setTileEntity(BlockPos pos, TileEntity tile){
		getOrCreateChunk(toChunkPos(pos)).setTileEntityG(pos, tile);
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue){
		return light.apply(EnumSkyBlock.SKY) << 20 | Math.max(light.apply(EnumSkyBlock.BLOCK), lightValue) << 4;
	}

	public void setLight(Function<EnumSkyBlock, Integer> light){
		this.light = light;
	}

	@Override
	public boolean isAirBlock(BlockPos pos){
		IBlockState state = getBlockState(pos);
		return state.getBlock().isAir(state, this, pos);
	}

	@Override
	public Biome getBiome(BlockPos pos){
		return biome;
	}

	public void setBiome(Biome biome){
		this.biome = biome;
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction){
		return getBlockState(pos).getStrongPower(this, pos, direction);
	}

	@Override
	public WorldType getWorldType(){
		return worldType;
	}

	public void setWorldType(WorldType worldType){
		this.worldType = worldType;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default){
		return getBlockState(pos).isSideSolid(this, pos, side);
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList chunks = new NBTTagList();
		this.chunks.forEach((pos, chunk) -> {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("pos", NBTUtil.createPosTag(pos));
			tag.setTag("chunk", chunk.serializeNBT());
			chunks.appendTag(tag);
		});
		nbt.setTag("chunks", chunks);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		this.chunks.clear();
		NBTTagList chunks = nbt.getTagList("chunks", 10);
		chunks.forEach(base -> {
			NBTTagCompound tag = (NBTTagCompound) base;
			BlockPos pos = NBTUtil.getPosFromTag(tag.getCompoundTag("pos"));
			Chunk chunk = createNewChunk(pos);
			chunk.deserializeNBT(tag.getCompoundTag("chunk"));
			this.chunks.put(pos, chunk);
		});
	}

	public class Chunk implements INBTSerializable<NBTTagCompound> {

		protected final BlockPos pos;

		protected boolean dirty;

		protected ChunkStorage<IBlockState, ?> blockStorage;
		protected Map<BlockPos, TileEntity> tiles = new HashMap<>();

		public Chunk(BlockPos pos, ChunkStorage<IBlockState, ?> blockStorage){
			this.pos = pos;
			this.blockStorage = blockStorage;
		}

		public Chunk(BlockPos pos){
			this(pos, new ChunkStorage<IBlockState, NBTTagInt>(chunkSize, new ChunkStorage.ChunkStorageSerializer.ContinuousChunkStorageSerializer(), state -> new NBTTagInt(state == null ? -1 : Block.getStateId(state)), id -> id.getInt() == -1 ? null : Block.getStateById(id.getInt())));
		}

		public BlockPos getChunkPos(){
			return pos;
		}

		public BlockPos getChunkOriginBlockPos(){
			return toChunkOriginBlockPos(pos);
		}

		public BlockPos toLocalBlockPos(BlockPos globalBlockPos){
			return globalBlockPos.subtract(getChunkOriginBlockPos());
		}

		public BlockPos toGlobalBlockPos(BlockPos localBlockPos){
			return localBlockPos.add(getChunkOriginBlockPos());
		}

		/*
		 * Dirty
		 */

		public boolean isDirty(){
			return dirty;
		}

		public void setDirty(boolean dirty){
			this.dirty = dirty;
		}

		/*
		 * Util
		 */

		public AxisAlignedBB getBoundingBox(){
			return new AxisAlignedBB(getChunkOriginBlockPos(), getChunkOriginBlockPos().add(chunkSize));
		}

		/*
		 * Data access
		 */

		public IBlockState getBlockStateL(BlockPos lpos){
			return blockStorage.get(lpos);
		}

		public IBlockState getBlockStateG(BlockPos gpos){
			return getBlockStateL(toLocalBlockPos(gpos));
		}

		public void setBlockStateL(BlockPos lpos, IBlockState state){
			blockStorage.set(lpos, state);
			setDirty(true);
		}

		public void setBlockStateG(BlockPos gpos, IBlockState state){
			setBlockStateL(toLocalBlockPos(gpos), state);
		}



		public TileEntity getTileEntityL(BlockPos lpos){
			return tiles.get(lpos);
		}

		public TileEntity getTileEntityG(BlockPos gpos){
			return getTileEntityL(toLocalBlockPos(gpos));
		}

		public void setTileEntityL(BlockPos lpos, TileEntity tile){
			tiles.put(lpos, tile);
			setDirty(true);
		}

		public void setTileEntityG(BlockPos gpos, TileEntity tile){
			setTileEntityL(toLocalBlockPos(gpos), tile);
		}

		/*
		 * NBT
		 */

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("blocks", blockStorage.serializeNBT());
			NBTTagList tiles = new NBTTagList();
			this.tiles.forEach((tilePos, tile) -> {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("pos", NBTUtil.createPosTag(tilePos));
				tag.setTag("tile", tile.serializeNBT());
				tiles.appendTag(tag);
			});
			nbt.setTag("tiles", tiles);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			blockStorage.deserializeNBT(nbt.getTag("blocks"));
			this.tiles.clear();
			NBTTagList tiles = nbt.getTagList("tiles", 10);
			tiles.forEach(base -> {
				NBTTagCompound tag = (NBTTagCompound) base;
				this.tiles.put(NBTUtil.getPosFromTag(tag.getCompoundTag("pos")), TileEntity.create(null, tag.getCompoundTag("tile")));
			});
		}
	}

	public static class ChunkStorage<T, NC extends NBTBase> implements INBTSerializable<NBTBase> {

		protected final Vec3i dimensions;
		protected final T[][][] ts;
		protected ChunkStorageSerializer serializer;
		protected Function<T, NC> componentSerializer;
		protected Function<NC, T> componentDeserializer;

		protected ChunkStorage(Vec3i dimensions){
			this.dimensions = dimensions;
			this.ts = (T[][][]) new Object[dimensions.getX()][dimensions.getY()][dimensions.getZ()];
		}

		public ChunkStorage(Vec3i dimensions, ChunkStorageSerializer<?> serializer, Function<T, NC> componentSerializer, Function<NC, T> componentDeserializer){
			this(dimensions);
			this.serializer = serializer;
			this.componentSerializer = componentSerializer;
			this.componentDeserializer = componentDeserializer;
		}

		public T get(Vec3i pos){
			return ts[pos.getX()][pos.getY()][pos.getZ()];
		}

		public void set(Vec3i pos, T t){
			ts[pos.getX()][pos.getY()][pos.getZ()] = t;
		}

		@Override
		public NBTBase serializeNBT(){
			return serializer.serialize(ts, dimensions, componentSerializer);
		}

		@Override
		public void deserializeNBT(NBTBase nbt){
			serializer.deserialize(ts, dimensions, nbt, componentDeserializer);
		}

		public interface ChunkStorageSerializer<NS extends NBTBase> {

			<T, NC extends NBTBase> NS serialize(T[][][] ts, Vec3i tsDimensions, Function<T, NC> componentSerializer);

			<T, NC extends NBTBase> void deserialize(T[][][] ts, Vec3i tsDimensions, NS nbt, Function<NC, T> componentDeserializer);

			class ContinuousChunkStorageSerializer implements ChunkStorageSerializer<NBTTagList> {

				@Override
				public <T, NC extends NBTBase> NBTTagList serialize(T[][][] ts, Vec3i tsDimensions, Function<T, NC> componentSerializer){
					NBTTagList list = new NBTTagList();
					for(int x = 0; x < tsDimensions.getX(); x++) for(int y = 0; y < tsDimensions.getY(); y++) for(int z = 0; z < tsDimensions.getZ(); z++) list.appendTag(componentSerializer.apply(ts[x][y][z]));
					return list;
				}

				@Override
				public <T, NC extends NBTBase> void deserialize(T[][][] ts, Vec3i tsDimensions, NBTTagList list, Function<NC, T> componentDeserializer){
					Iterator<NC> iterator = (Iterator) list.iterator();
					xyz: for(int x = 0; x < tsDimensions.getX(); x++) for(int y = 0; y < tsDimensions.getY(); y++) for(int z = 0; z < tsDimensions.getZ(); z++) if(iterator.hasNext()) ts[x][y][z] = componentDeserializer.apply(iterator.next()); else break xyz;
				}
			}

			class PaletteChunkStorageSerializer implements ChunkStorageSerializer<NBTTagCompound> {

				@Override
				public <T, NC extends NBTBase> NBTTagCompound serialize(T[][][] ts, Vec3i tsDimensions, Function<T, NC> componentSerializer){
					NBTTagCompound nbt = new NBTTagCompound();
					List<T> palette = new ArrayList<>();
					List<Integer> data = new ArrayList<>(tsDimensions.getX() * tsDimensions.getY() * tsDimensions.getZ());
					for(int x = 0; x < tsDimensions.getX(); x++) for(int y = 0; y < tsDimensions.getY(); y++) for(int z = 0; z < tsDimensions.getZ(); z++){
						T t = ts[x][y][z];
						int index = palette.indexOf(t);
						if(index < 0){
							index = palette.size();
							palette.add(t);
						}
						data.add(index);
					}
					nbt.setTag("data", new NBTTagIntArray(data));
					NBTTagList list = new NBTTagList();
					for(T t : palette) list.appendTag(componentSerializer.apply(t));
					nbt.setTag("palette", list);
					return nbt;
				}

				@Override
				public <T, NC extends NBTBase> void deserialize(T[][][] ts, Vec3i tsDimensions, NBTTagCompound nbt, Function<NC, T> componentDeserializer){
					NBTTagList list = (NBTTagList) nbt.getTag("palette");
					List<T> palette = new ArrayList<>();
					for(NBTBase tag : list) palette.add(componentDeserializer.apply((NC) tag));
					int[] data = nbt.getIntArray("data");
					int index = 0;
					for(int x = 0; x < tsDimensions.getX(); x++) for(int y = 0; y < tsDimensions.getY(); y++) for(int z = 0; z < tsDimensions.getZ(); z++){
						ts[x][y][z] = palette.get(data[index]);
						index++;
					}
				}
			}

		}

	}

}
