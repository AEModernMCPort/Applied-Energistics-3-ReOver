
package appeng.core.api.material;


import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * In most cases, a material is an item without other purposes than being converted into another item (another material or something actually useful), be it using crafting, processing or any other imaginable way.<br>
 * This class allows you to create new material items without clogging item ids - it will use a single AE item for all the materials.<br>
 * <br>
 * A material is registered just like you would register any other {@linkplain IForgeRegistryEntry} thing - {@link GameRegistry#register(IForgeRegistryEntry)}.
 * 
 * @author Elix_x
 *
 */
public class Material extends IForgeRegistryEntry.Impl<Material>
{

	private ModelResourceLocation model;
	private String unlocalizedName;

	/**
	 * Sets the default model location.
	 * 
	 * @param model - new default model location
	 */
	public void setModel( ModelResourceLocation model )
	{
		this.model = model;
	}

	/**
	 * Retrieves model location based on the stack.
	 * 
	 * @param itemstack - stack to get model for
	 * @return model location for the given stack
	 */
	@Nonnull
	public ModelResourceLocation getModel( ItemStack itemstack )
	{
		return model;
	}

	/**
	 * Sets the default unlocalized name.
	 * 
	 * @param unlocalizedName - new default unlocalized name
	 */
	public void setUnlocalizedName( String unlocalizedName )
	{
		this.unlocalizedName = unlocalizedName;
	}

	/**
	 * Retrieves unlocalized name based on the item stack.<br>
	 * <b>It will be localized by prefixing <tt>material.</tt></b> and not <tt>item.</tt>!
	 * 
	 * @param itemstack - stack to get unlocalized name for
	 * @return unlocalized name for the given stack
	 */
	@Nonnull
	public String getUnlocalizedName( ItemStack itemstack )
	{
		return unlocalizedName;
	}

	/**
	 * Retrieves display name for the item stack. Returning <tt>null</tt> will cause {@linkplain #getUnlocalizedName(ItemStack)} to be called and localized.
	 * 
	 * @param itemstack - stack to get display name for
	 * @return display name for the given stack, or null if {@linkplain #getUnlocalizedName(ItemStack)} should be used instead
	 */
	@Nullable
	public String getDisplayName( ItemStack itemstack )
	{
		return null;
	}

	// Vanilla Copy Pasta

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public EnumActionResult onItemUse( EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ )
	{
		return EnumActionResult.PASS;
	}

	public float getStrVsBlock( ItemStack stack, IBlockState state )
	{
		return 1.0F;
	}

	public ActionResult<ItemStack> onItemRightClick( World worldIn, EntityPlayer playerIn, EnumHand handIn )
	{
		return new ActionResult( EnumActionResult.PASS, playerIn.getHeldItem( handIn ) );
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	public ItemStack onItemUseFinish( ItemStack stack, World worldIn, EntityLivingBase entityLiving )
	{
		return stack;
	}

	public boolean hitEntity( ItemStack stack, EntityLivingBase target, EntityLivingBase attacker )
	{
		return false;
	}

	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
	 */
	public boolean onBlockDestroyed( ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving )
	{
		return false;
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 */
	public boolean itemInteractionForEntity( ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand )
	{
		return false;
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
	 * update it's contents.
	 */
	public void onUpdate( ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected )
	{
	}

	/**
	 * Called when item is crafted/smelted. Used only by maps so far.
	 */
	public void onCreated( ItemStack stack, World worldIn, EntityPlayer playerIn )
	{
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction( ItemStack stack )
	{
		return EnumAction.NONE;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration( ItemStack stack )
	{
		return 0;
	}

	/**
	 * Called when the player stops using an Item (stops holding the right mouse button).
	 */
	public void onPlayerStoppedUsing( ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft )
	{
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@SideOnly( Side.CLIENT )
	public void addInformation( ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced )
	{
	}

	/**
	 * Returns true if this item has an enchantment glint. By default, this returns
	 * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
	 * true).
	 * 
	 * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
	 * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
	 */
	@SideOnly( Side.CLIENT )
	public boolean hasEffect( ItemStack stack )
	{
		return false;
	}

	/**
	 * Return an item rarity from EnumRarity
	 */
	@Nonnull
	public EnumRarity getRarity( ItemStack stack )
	{
		return EnumRarity.COMMON;
	}

	/**
	 * ItemStack sensitive version of getItemAttributeModifiers
	 */
	@Nonnull
	public Multimap<String, AttributeModifier> getAttributeModifiers( EntityEquipmentSlot slot, ItemStack stack )
	{
		return HashMultimap.create();
	}

	/**
	 * Called when a player drops the item into the world,
	 * returning false from this will prevent the item from
	 * being removed from the players inventory and spawning
	 * in the world
	 *
	 * @param player The player that dropped the item
	 * @param item The item stack, before the item is removed.
	 */
	public boolean onDroppedByPlayer( ItemStack item, EntityPlayer player )
	{
		return true;
	}

	/**
	 * Allow the item one last chance to modify its name used for the
	 * tool highlight useful for adding something extra that can't be removed
	 * by a user in the displayed name, such as a mode of operation.
	 *
	 * @param item the ItemStack for the item.
	 * @param displayName the name that will be displayed unless it is changed in this method.
	 */
	public String getHighlightTip( ItemStack item, String displayName )
	{
		return displayName;
	}

	/**
	 * This is called when the item is used, before the block is activated.
	 * 
	 * @param stack The Item Stack
	 * @param player The Player that used the item
	 * @param world The Current World
	 * @param pos Target position
	 * @param side The side of the target hit
	 * @param hand Which hand the item is being held in.
	 * @return Return PASS to allow vanilla handling, any other to skip normal code.
	 */
	public EnumActionResult onItemUseFirst( EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand )
	{
		return EnumActionResult.PASS;
	}

	/**
	 * Called before a block is broken. Return true to prevent default block harvesting.
	 *
	 * Note: In SMP, this is called on both client and server sides!
	 *
	 * @param itemstack The current ItemStack
	 * @param pos Block's position in world
	 * @param player The Player that is wielding the item
	 * @return True to prevent harvesting, false to continue as normal
	 */
	public boolean onBlockStartBreak( ItemStack itemstack, BlockPos pos, EntityPlayer player )
	{
		return false;
	}

	/**
	 * Called each tick while using an item.
	 * 
	 * @param stack The Item being used
	 * @param player The Player using the item
	 * @param count The amount of time in tick the item has been used for continuously
	 */
	public void onUsingTick( ItemStack stack, EntityLivingBase player, int count )
	{
	}

	/**
	 * Called when the player Left Clicks (attacks) an entity.
	 * Processed before damage is done, if return value is true further processing is canceled
	 * and the entity is not attacked.
	 *
	 * @param stack The Item being used
	 * @param player The player that is attacking
	 * @param entity The entity being attacked
	 * @return True to cancel the rest of the interaction.
	 */
	public boolean onLeftClickEntity( ItemStack stack, EntityPlayer player, Entity entity )
	{
		return false;
	}

	/**
	 * ItemStack sensitive version of getContainerItem.
	 * Returns a full ItemStack instance of the result.
	 *
	 * @param itemStack The current ItemStack
	 * @return The resulting ItemStack
	 */
	public ItemStack getContainerItem( ItemStack itemStack )
	{
		return ItemStack.EMPTY;
	}

	/**
	 * ItemStack sensitive version of hasContainerItem
	 * 
	 * @param stack The current item stack
	 * @return True if this item has a 'container'
	 */
	public boolean hasContainerItem( ItemStack stack )
	{
		/**
		 * True if this Item has a container item (a.k.a. crafting result)
		 */
		return false;
	}

	/**
	 * Retrieves the normal 'lifespan' of this item when it is dropped on the ground as a EntityItem.
	 * This is in ticks, standard result is 6000, or 5 mins.
	 *
	 * @param itemStack The current ItemStack
	 * @param world The world the entity is in
	 * @return The normal lifespan in ticks.
	 */
	public int getEntityLifespan( ItemStack itemStack, World world )
	{
		return 6000;
	}

	/**
	 * Determines if this Item has a special entity for when they are in the world.
	 * Is called when a EntityItem is spawned in the world, if true and Item#createCustomEntity
	 * returns non null, the EntityItem will be destroyed and the new Entity will be added to the world.
	 *
	 * @param stack The current item stack
	 * @return True of the item has a custom entity, If true, Item#createCustomEntity will be called
	 */
	public boolean hasCustomEntity( ItemStack stack )
	{
		return false;
	}

	/**
	 * This function should return a new entity to replace the dropped item.
	 * Returning null here will not kill the EntityItem and will leave it to function normally.
	 * Called when the item it placed in a world.
	 *
	 * @param world The world object
	 * @param location The EntityItem object, useful for getting the position of the entity
	 * @param itemstack The current item stack
	 * @return A new Entity object to spawn or null
	 */
	@Nullable
	public Entity createEntity( World world, Entity location, ItemStack itemstack )
	{
		return null;
	}

	/**
	 * Called by the default implemetation of EntityItem's onUpdate method, allowing for cleaner
	 * control over the update of the item without having to write a subclass.
	 *
	 * @param entityItem The entity Item
	 * @return Return true to skip any further update code.
	 */
	public boolean onEntityItemUpdate( net.minecraft.entity.item.EntityItem entityItem )
	{
		return false;
	}

	/**
	 * Determines the base experience for a player when they remove this item from a furnace slot.
	 * This number must be between 0 and 1 for it to be valid.
	 * This number will be multiplied by the stack size to get the total experience.
	 *
	 * @param item The item stack the player is picking up.
	 * @return The amount to award for each item.
	 */
	public float getSmeltingExperience( ItemStack item )
	{
		return -1; // -1 will default to the old lookups.
	}

	/**
	 *
	 * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
	 *
	 * @param world The world
	 * @param pos Block position in world
	 * @param player The Player that is wielding the item
	 * @return
	 */
	public boolean doesSneakBypassUse( ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player )
	{
		return false;
	}

	/**
	 * Returns the font renderer used to render tooltips and overlays for this item.
	 * Returning null will use the standard font renderer.
	 *
	 * @param stack The current item stack
	 * @return A instance of FontRenderer or null to use default
	 */
	@SideOnly( Side.CLIENT )
	@Nullable
	public net.minecraft.client.gui.FontRenderer getFontRenderer( ItemStack stack )
	{
		return null;
	}

	/**
	 * Called when a entity tries to play the 'swing' animation.
	 *
	 * @param entityLiving The entity swinging the item.
	 * @param stack The Item stack
	 * @return True to cancel any further processing by EntityLiving
	 */
	public boolean onEntitySwing( EntityLivingBase entityLiving, ItemStack stack )
	{
		return false;
	}

	/**
	 * Gets the maximum number of items that this stack should be able to hold.
	 * This is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
	 *
	 * @param stack The ItemStack
	 * @return The maximum number this item can be stacked to
	 */
	public int getItemStackLimit( ItemStack stack )
	{
		return 64;
	}

	/**
	 * ItemStack sensitive version of {@link #canHarvestBlock(IBlockState)}
	 * 
	 * @param state The block trying to harvest
	 * @param stack The itemstack used to harvest the block
	 * @return true if can harvest the block
	 */
	public boolean canHarvestBlock( IBlockState state, ItemStack stack )
	{
		/**
		 * Check whether this Item can harvest the given Block
		 */
		return false;
	}

	/**
	 * Queries the harvest level of this item stack for the specified tool class,
	 * Returns -1 if this tool is not of the specified type
	 *
	 * @param stack This item stack instance
	 * @param toolClass Tool Class
	 * @param player The player trying to harvest the given blockstate
	 * @param blockState The block to harvest
	 * @return Harvest level, or -1 if not the specified tool type.
	 */
	public int getHarvestLevel( ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState )
	{
		return -1;
	}

	/**
	 * Whether this Item can be used as a payment to activate the vanilla beacon.
	 * 
	 * @param stack the ItemStack
	 * @return true if this Item can be used
	 */
	public boolean isBeaconPayment( ItemStack stack )
	{
		return false;
	}

	/**
	 * Determine if the player switching between these two item stacks
	 * 
	 * @param oldStack The old stack that was equipped
	 * @param newStack The new stack
	 * @param slotChanged If the current equipped slot was changed,
	 * Vanilla does not play the animation if you switch between two
	 * slots that hold the exact same item.
	 * @return True to play the item change animation
	 */
	public boolean shouldCauseReequipAnimation( ItemStack oldStack, ItemStack newStack, boolean slotChanged )
	{
		return !oldStack.equals( newStack ); // !ItemStack.areItemStacksEqual(oldStack, newStack);
	}

}
