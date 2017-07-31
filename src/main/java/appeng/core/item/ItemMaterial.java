package appeng.core.item;

import appeng.api.item.IStateItem;
import appeng.api.item.IStateItemState;
import appeng.api.item.IStateItemState.Property;
import appeng.core.core.api.item.IItemMaterial;
import appeng.core.core.api.material.Material;
import appeng.core.core.AppEngCore;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMaterial extends Item implements IItemMaterial<ItemMaterial>, IStateItem<ItemMaterial> {

	public static final ForgeRegistry<Material> REGISTRY = AppEngCore.INSTANCE.getMaterialRegistry();

	public static enum MaterialProperty implements IStateItemState.Property<Material> {

		INSTANCE;

		@Override
		public String getName(){
			return "material";
		}

		@Override
		public boolean isValid(Material value){
			return REGISTRY.containsValue(value);
		}

	}

	public ItemMaterial(){
		setHasSubtypes(true);
	}

	@Override
	public boolean isValid(Property property){
		return property == ItemMaterial.MaterialProperty.INSTANCE;
	}

	@Override
	public Property getProperty(String name){
		return name.equals(ItemMaterial.MaterialProperty.INSTANCE.getName()) ? ItemMaterial.MaterialProperty.INSTANCE : null;
	}

	@Override
	public IStateItemState<ItemMaterial> getState(ItemStack itemstack){
		return new IStateItemState<>(this).withProperty(ItemMaterial.MaterialProperty.INSTANCE, REGISTRY.getValue(itemstack.getMetadata()));
	}

	@Override
	public IStateItemState<ItemMaterial> getDefaultState(){
		return new IStateItemState<ItemMaterial>(this).withProperty(MaterialProperty.INSTANCE, REGISTRY.getValue(0));
	}

	@Override
	public ItemStack getItemStack(IStateItemState<ItemMaterial> state, int amount){
		return new ItemStack(this, amount, REGISTRY.getID(state.getValue(ItemMaterial.MaterialProperty.INSTANCE)));
	}

	/**
	 * Helper method for {@linkplain ItemStack} to {@linkplain Material} conversion.
	 *
	 * @param itemstack to convert
	 * @return {@linkplain Material} corresponding to given stack.
	 */
	public Material getMaterial(ItemStack itemstack){
		return getState(itemstack).getValue(ItemMaterial.MaterialProperty.INSTANCE);
	}

	/**
	 * Helper method for {@linkplain Material} to {@linkplain ItemStack} conversion.
	 *
	 * @param material to convert
	 * @param amount   of resulting items in stack
	 * @return {@linkplain ItemStack} corresponding to given material containing given amount of items.
	 */
	public ItemStack getItemStack(Material material, int amount){
		return getItemStack(new IStateItemState<ItemMaterial>(this).withProperty(ItemMaterial.MaterialProperty.INSTANCE, material), amount);
	}

	/*
	 * OVERRIDES START
	 */

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems){
		for(Material material : REGISTRY){
			subItems.add(getItemStack(material, 1));
		}
	}

	@Override
	public String getUnlocalizedName(){
		return "material.null";
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack){
		return "material." + getMaterial(itemstack).getUnlocalizedName();
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack){
		String display = getMaterial(itemstack).getDisplayName();
		return display != null ? display : super.getItemStackDisplayName(itemstack);
	}

	// Vanilla Copy Pasta

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		return getMaterial(player.getHeldItem(hand)).onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state){
		return getMaterial(stack).getStrVsBlock(state);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
		return super.onItemRightClick(worldIn,playerIn,handIn);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving){
		getMaterial(stack).onItemUseFinish(worldIn, entityLiving);
		return stack;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker){
		return getMaterial(stack).hitEntity(target, attacker);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving){
		return getMaterial(stack).onBlockDestroyed(worldIn, state, pos, entityLiving);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand){
		return getMaterial(stack).itemInteractionForEntity(playerIn, target, hand);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		getMaterial(stack).onUpdate(worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn){
		getMaterial(stack).onCreated(worldIn, playerIn);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack){
		return getMaterial(stack).getItemUseAction();
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack){
		return getMaterial(stack).getMaxItemUseDuration();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft){
		getMaterial(stack).onPlayerStoppedUsing(worldIn, entityLiving, timeLeft);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		getMaterial(stack).addInformation(worldIn, tooltip, flagIn);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack){
		return getMaterial(stack).hasEffect();
	}

	@Override
	public EnumRarity getRarity(ItemStack stack){
		return getMaterial(stack).getRarity();
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack){
		return getMaterial(stack).getAttributeModifiers(slot);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player){
		return getMaterial(item).onDroppedByPlayer(player);
	}

	@Override
	public String getHighlightTip(ItemStack item, String displayName){
		return getMaterial(item).getHighlightTip(displayName);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand){
		return getMaterial(player.getHeldItem(hand)).onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player){
		return getMaterial(itemstack).onBlockStartBreak(pos, player);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		getMaterial(stack).onUsingTick(player, count);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity){
		return getMaterial(stack).onLeftClickEntity(player, entity);
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack){
		return itemStack;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack){
		return getMaterial(stack).hasContainerItem();
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world){
		return getMaterial(itemStack).getEntityLifespan(world);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack){
		return getMaterial(stack).hasCustomEntity();
	}

	@Nullable
	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack){
		return getMaterial(itemstack).createEntity(world, location);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem){
		return getMaterial(entityItem.getItem()).onEntityItemUpdate(entityItem);
	}

	@Override
	public float getSmeltingExperience(ItemStack item){
		return getMaterial(item).getSmeltingExperience();
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player){
		return getMaterial(stack).doesSneakBypassUse(world, pos, player);
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack){
		return getMaterial(stack).getFontRenderer();
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack){
		return getMaterial(stack).onEntitySwing(entityLiving);
	}

	@Override
	public int getItemStackLimit(ItemStack stack){
		return getMaterial(stack).getItemStackLimit();
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack){
		return getMaterial(stack).canHarvestBlock(state);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState){
		return getMaterial(stack).getHarvestLevel(toolClass, player, blockState);
	}

	@Override
	public boolean isBeaconPayment(ItemStack stack){
		return getMaterial(stack).isBeaconPayment();
	}

}
