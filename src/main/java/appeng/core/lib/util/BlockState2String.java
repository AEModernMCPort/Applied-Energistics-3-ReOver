package appeng.core.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.InvalidBlockStateException;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockState2String {

	public static final String FORMAT = "%1$s[%2$s]";
	public static final String PROPFORMAT = "%1$s=%2$s";

	public static boolean isValidBlock(ResourceLocation id){
		return id.equals(Block.REGISTRY.getObject(null).getRegistryName()) || Block.REGISTRY.getObject(id) != Blocks.AIR;
	}

	@Nullable
	public static Block getBlockOrNull(ResourceLocation id){
		return isValidBlock(id) ? Block.REGISTRY.getObject(id) : null;
	}

	public static String toString(IBlockState state){
		Block block = state.getBlock();
		List<String> stateSs = new ArrayList<>();
		for(Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()){
			Map.Entry<IProperty, Comparable> e = (Map.Entry) entry;
			stateSs.add(String.format(PROPFORMAT, e.getKey().getName(), e.getKey().getName(e.getValue())));
		}
		return String.format(FORMAT, block.getRegistryName(), String.join(",", stateSs));
	}

	public static IBlockState fromString(String s){
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s.split("\\{")[0]));
		if(block == null) throw new IllegalArgumentException("Invalid block state string \"" + s + "\". Block not found.");
		try {
			String state = s.contains("{") ? s.split("\\{")[1].replace("}", "") : "";
			return state.isEmpty() ? block.getDefaultState() : CommandBase.convertArgToBlockState(block, state);
		} catch(NumberInvalidException | InvalidBlockStateException e){
			throw new IllegalArgumentException("Invalid block state string \"" + s + "\". State could not be parsed.", e);
		}
	}

	public static Optional<IBlockState> fromStringSafe(String s){
		String state = s.contains("{") ? s.split("\\{")[1].replace("}", "") : "";
		return Optional.ofNullable(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s.split("\\{")[0]))).map(block -> state.isEmpty() ? block.getDefaultState() : convertArgToBlockStateSafe(block, state));
	}

	public static boolean isValidBlockState(String s){
		return fromStringSafe(s).isPresent();
	}

	private static IBlockState convertArgToBlockStateSafe(Block block, String state){
		try {
			return CommandBase.convertArgToBlockState(block, state);
		} catch(NumberInvalidException | InvalidBlockStateException e){
			return null;
		}
	}

}
