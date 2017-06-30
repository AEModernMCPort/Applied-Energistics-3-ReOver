package appeng.core.skyfall.block;

import appeng.core.lib.util.OptionalUtil;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.config.SkyfallConfig;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Collection;

public class CertusInfusedBlock extends Block {

	public static final CertusInfusedProperty BLOCK = new CertusInfusedProperty("block");

	private static SkyfallConfig.Meteorite config = AppEngSkyfall.INSTANCE.config.meteorite;

	public static void recompile(SkyfallConfig.Meteorite config){
		CertusInfusedBlock.config = config;
		BLOCK.recompile();
	}

	public CertusInfusedBlock(){
		super(Material.GROUND);
	}

	@Override
	public BlockStateContainer getBlockState(){
		return new BlockStateContainer(this, BLOCK);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(BLOCK, BLOCK.states.get(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return BLOCK.states.indexOf(state.getValue(BLOCK));
	}

	@Override
	public int damageDropped(IBlockState state){
		return getMetaFromState(state);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items){
		for(int i = 0; i < BLOCK.states.size(); i++) items.add(new ItemStack(this, 1, i));
	}

	public static class CertusInfusedProperty extends PropertyHelper<CertusInfusedProperty.IBlockStateWrapper> {

		private ImmutableList<IBlockStateWrapper> states;

		private CertusInfusedProperty(String name){
			super(name, IBlockStateWrapper.class);
			recompile();
		}

		private void recompile(){
			states = ImmutableList.copyOf(Lists.transform(config.allowedBlockStatesList(), IBlockStateWrapper::new));
		}

		@Override
		public Collection<IBlockStateWrapper> getAllowedValues(){
			return states;
		}

		@Override
		public Optional<IBlockStateWrapper> parseValue(String value){
			return Optional.fromJavaUtil(OptionalUtil.tryOrEmpty(() -> states.get(Integer.valueOf(value))));
		}

		@Override
		public String getName(IBlockStateWrapper value){
			return Integer.toString(states.indexOf(value));
		}

		public static class IBlockStateWrapper implements Comparable<IBlockStateWrapper> {

			public final IBlockState state;

			public IBlockStateWrapper(IBlockState state){
				this.state = state;
			}

			@Override
			public int compareTo(IBlockStateWrapper o){
				return 0;
			}

		}
	}

}
