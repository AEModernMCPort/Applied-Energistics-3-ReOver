package appeng.core.skyfall.block;

import appeng.core.skyfall.config.SkyfallConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public class CertusInfusedBlock extends Block {

	private static SkyfallConfig.Meteorite config = new SkyfallConfig.Meteorite();

//	public static final CertusInfusedProperty VARIANT = new CertusInfusedProperty("block");
	public static final IProperty<CertusInfusedPropertyEnum> VARIANT = PropertyEnum.create("variant", CertusInfusedPropertyEnum.class);


	public static void recompile(SkyfallConfig.Meteorite config){
		CertusInfusedBlock.config = config;
	}

	public CertusInfusedBlock(){
		super(Material.GROUND);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public BlockStateContainer getBlockState(){
		return new BlockStateContainer(this, VARIANT);
	}

	/*@Override
	public IBlockState getStateFromMeta(int meta){
		return super.getStateFromMeta(meta).withProperty(VARIANT, VARIANT.states.get(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return VARIANT.states.indexOf(state.getValue(VARIANT));
	}*/

	@Override
	public IBlockState getStateFromMeta(int meta){
		return super.getStateFromMeta(meta).withProperty(VARIANT, CertusInfusedPropertyEnum.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(VARIANT).ordinal();
	}

	/*@Override
	public int damageDropped(IBlockState state){
		return getMetaFromState(state);
	}*/

	/*@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items){
		for(int i = 0; i < VARIANT.states.size(); i++) items.add(new ItemStack(this, 1, i));
	}*/

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items){
		for(int i = 0; i < CertusInfusedPropertyEnum.values().length; i++) items.add(new ItemStack(this, 1, i));
	}

	public String getDisplayName(IBlockState state){
		IBlockState pState = state.getValue(VARIANT).state;
		return Item.getItemFromBlock(pState.getBlock()).getItemStackDisplayName(new ItemStack(pState.getBlock(), 1, pState.getBlock().getMetaFromState(state)));
	}

	public enum CertusInfusedPropertyEnum implements IStringSerializable {

		STONE(Blocks.STONE.getDefaultState());

		public IBlockState state;

		CertusInfusedPropertyEnum(IBlockState state){
			this.state = state;
		}

		@Override
		public String getName(){
			return name().toLowerCase();
		}
	}

	/*public static class CertusInfusedProperty extends PropertyHelper<CertusInfusedProperty.IBlockStateWrapper> {

		private ImmutableList<IBlockStateWrapper> states;

		private CertusInfusedProperty(String name){
			super(name, IBlockStateWrapper.class);
			ImmutableList.Builder<IBlockStateWrapper> statesBuilder = ImmutableList.builder();
			for(int i = 0; i < 16; i++) statesBuilder.add(new IBlockStateWrapper(Blocks.AIR.getDefaultState()));
			states = statesBuilder.build();
		}

		private void recompile(){
			states = ImmutableList.copyOf(config.allowedBlocks.stream().map(s -> new IBlockStateWrapper(BlockState2String.fromString(s))).collect(Collectors.toList()));
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

		public class IBlockStateWrapper implements Comparable<IBlockStateWrapper> {

			public final IBlockState state;

			public IBlockStateWrapper(IBlockState state){
				this.state = state;
			}

			@Override
			public int compareTo(IBlockStateWrapper o){
				return states.indexOf(this) - states.indexOf(o);
			}

		}
	}*/

}
