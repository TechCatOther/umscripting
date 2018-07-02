package org.ultramine.mods.scripting.event.proxy.special;

import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import groovy.lang.Script;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.ultramine.mods.scripting.event.IBlockTypesAttachable;
import org.ultramine.mods.scripting.event.IPlayerAttachable;
import org.ultramine.mods.scripting.event.IWorldBlockAttachable;
import org.ultramine.mods.scripting.event.proxy.FilterEventRegProxy;
import org.ultramine.mods.scripting.mcutil.BlockState;
import org.ultramine.mods.scripting.mcutil.BlockTypes;
import org.ultramine.mods.scripting.mcutil.GameUtil;
import org.ultramine.mods.scripting.mcutil.WorldBlock;
import org.ultramine.server.util.InventoryUtil;

public class PlayerInteractEventRegProxy extends FilterEventRegProxy<PlayerInteractEvent> implements IPlayerAttachable, IBlockTypesAttachable, IWorldBlockAttachable
{
	private final PlayerInteractEvent.Action action;

	public PlayerInteractEventRegProxy(Script script, EventPriority prior, final PlayerInteractEvent.Action action)
	{
		super(script, PlayerInteractEvent.class, prior);
		this.action = action;
		filter(e -> e.action == action);
	}

	@Override
	public PlayerInteractEventRegProxy by(EntityPlayerMP player)
	{
		attachToEntity(player);
		final int id = player.getEntityId();
		filter(e -> e.entityPlayer.getEntityId() == id);
		return this;
	}

//	public PlayerInteractEventRegProxy by(GameProfile player)
//	{
//		final UUID id = player.getId();
//		filter(e -> e.entityPlayer.getGameProfile().getId().equals(id));
//		return this;
//	}

	public PlayerInteractEventRegProxy with(Block block)
	{
		if(action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
			throw new IllegalStateException("Can't specify block when handling RIGHT_CLICK_AIR");
		filter(e -> e.world.getBlock(e.x, e.y, e.z) == block);
		return this;
	}

	public PlayerInteractEventRegProxy with(BlockTypes block)
	{
		if(action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
			throw new IllegalStateException("Can't specify block when handling RIGHT_CLICK_AIR");
		filter(e -> block.isType(e.world.getBlock(e.x, e.y, e.z), e.world.getBlockMetadata(e.x, e.y, e.z)));
		return this;
	}

	public PlayerInteractEventRegProxy with(BlockState block)
	{
		if(action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
			throw new IllegalStateException("Can't specify block when handling RIGHT_CLICK_AIR");
		filter(e -> block.isType(e.world.getBlock(e.x, e.y, e.z), e.world.getBlockMetadata(e.x, e.y, e.z)));
		return this;
	}

	public PlayerInteractEventRegProxy use(ItemStack stack)
	{
		filter(e -> {
			ItemStack is = e.entityPlayer.inventory.getCurrentItem();
			return is != null && InventoryUtil.isStacksEquals(is, stack);
		});
		return this;
	}

	public PlayerInteractEventRegProxy use(Item item)
	{
		filter(e -> {
			ItemStack is = e.entityPlayer.inventory.getCurrentItem();
			return is != null && is.getItem() == item;
		});
		return this;
	}

	@Override
	public PlayerInteractEventRegProxy at(WorldBlock block)
	{
		if(action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
			throw new IllegalStateException("Can't specify block when handling RIGHT_CLICK_AIR");
		filter(e -> e.world.provider.dimensionId == block.getWorld().getDimension() && e.x == block.x && e.y == block.y && e.z == block.z);
		return this;
	}

	//aliases

	public void by(EntityPlayerMP player, Closure<Void> clsr)
	{
		by(player).call(clsr);
	}

	public PlayerInteractEventRegProxy with(String block)
	{
		return with(GameUtil.getBlock(block));
	}

	public PlayerInteractEventRegProxy with(int block)
	{
		return with(GameUtil.getBlock(block));
	}

	public void with(Block block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public void with(String block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public void with(int block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public void with(BlockTypes block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public void with(BlockState block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public PlayerInteractEventRegProxy to(Block block)
	{
		return with(block);
	}

	public PlayerInteractEventRegProxy to(String block)
	{
		return with(block);
	}

	public PlayerInteractEventRegProxy to(int block)
	{
		return with(block);
	}

	public PlayerInteractEventRegProxy to(BlockTypes block)
	{
		return with(block);
	}

	public PlayerInteractEventRegProxy to(BlockState block)
	{
		return with(block);
	}

	public void to(Block block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public void to(String block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public void to(int block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public void to(BlockTypes block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public void to(BlockState block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	@Override
	public PlayerInteractEventRegProxy block(BlockTypes block)
	{
		return with(block);
	}

	public void block(BlockTypes block, Closure<Void> clsr)
	{
		with(block).call(clsr);
	}

	public PlayerInteractEventRegProxy use(String item)
	{
		return use(GameUtil.getItem(item));
	}

	public PlayerInteractEventRegProxy use(int item)
	{
		return use(GameUtil.getItem(item));
	}

	public void use(ItemStack stack, Closure<Void> clsr)
	{
		use(stack).call(clsr);
	}

	public void use(Item item, Closure<Void> clsr)
	{
		use(item).call(clsr);
	}

	public void use(String item, Closure<Void> clsr)
	{
		use(item).call(clsr);
	}

	public void use(int item, Closure<Void> clsr)
	{
		use(item).call(clsr);
	}

	public void at(WorldBlock block, Closure<Void> clsr)
	{
		at(block).call(clsr);
	}
}
