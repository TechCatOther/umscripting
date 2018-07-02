package org.ultramine.mods.scripting.event.proxy.special;

import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import groovy.lang.Script;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import org.ultramine.mods.scripting.event.IBlockTypesAttachable;
import org.ultramine.mods.scripting.event.IPlayerAttachable;
import org.ultramine.mods.scripting.event.IWorldBlockAttachable;
import org.ultramine.mods.scripting.event.proxy.FilterEventRegProxy;
import org.ultramine.mods.scripting.mcutil.BlockTypes;
import org.ultramine.mods.scripting.mcutil.GameUtil;
import org.ultramine.mods.scripting.mcutil.WorldBlock;

public class BlockEventRegProxy extends FilterEventRegProxy<BlockEvent> implements IPlayerAttachable, IBlockTypesAttachable, IWorldBlockAttachable
{
	public BlockEventRegProxy(Script script, Class<? extends BlockEvent> eventType, EventPriority prior)
	{
		super(script, eventType, prior);
	}

	public BlockEventRegProxy by(EntityPlayerMP player)
	{
		attachToEntity(player);
		final int id = player.getEntityId();
		if(eventType == BlockEvent.HarvestDropsEvent.class)
			filter(e -> ((BlockEvent.HarvestDropsEvent)e).harvester != null && ((BlockEvent.HarvestDropsEvent)e).harvester.getEntityId() == id);
		else if(eventType == BlockEvent.BreakEvent.class)
			filter(e -> ((BlockEvent.BreakEvent)e).getPlayer() != null && ((BlockEvent.BreakEvent)e).getPlayer().getEntityId() == id);
		else if(eventType == BlockEvent.PlaceEvent.class || eventType == BlockEvent.MultiPlaceEvent.class)
			filter(e -> ((BlockEvent.PlaceEvent)e).player != null && ((BlockEvent.PlaceEvent)e).player.getEntityId() == id);
		else
			throw new RuntimeException("unknown BlockEvent class: " + eventType.getName());
		return this;
	}

	public BlockEventRegProxy block(Block block)
	{
		if(eventType == BlockEvent.MultiPlaceEvent.class)
			filter(e -> {
				for(BlockSnapshot bs : ((BlockEvent.MultiPlaceEvent) e).getReplacedBlockSnapshots())
					if(bs.getCurrentBlock() == block)
						return true;
				return false;
			});
		else
			filter(e -> e.block == block);
		return this;
	}

	public BlockEventRegProxy block(String block)
	{
		return block(GameUtil.getBlock(block));
	}

	public BlockEventRegProxy block(int block)
	{
		return block(GameUtil.getBlock(block));
	}

	@Override
	public BlockEventRegProxy block(BlockTypes block)
	{
		if(eventType == BlockEvent.MultiPlaceEvent.class)
			filter(e -> {
				for(BlockSnapshot bs : ((BlockEvent.MultiPlaceEvent) e).getReplacedBlockSnapshots())
					if(block.isType(bs.getCurrentBlock(), bs.world.getBlockMetadata(bs.x, bs.y, bs.z)))
						return true;
				return false;
			});
		else
			filter(e -> block.isType(e.block, e.blockMetadata));
		return this;
	}

	@Override
	public BlockEventRegProxy at(WorldBlock block)
	{
		filter(e -> e.world.provider.dimensionId == block.getWorld().getDimension() && e.x == block.x && e.y == block.y && e.z == block.z);
		return this;
	}

	//aliases

	public void by(EntityPlayerMP player, Closure<Void> clsr)
	{
		by(player).call(clsr);
	}

	public void block(Block block, Closure<Void> clsr)
	{
		block(block).call(clsr);
	}

	public void block(String block, Closure<Void> clsr)
	{
		block(block).call(clsr);
	}

	public void block(int block, Closure<Void> clsr)
	{
		block(block).call(clsr);
	}

	public void block(BlockTypes block, Closure<Void> clsr)
	{
		block(block).call(clsr);
	}

	public void at(WorldBlock block, Closure<Void> clsr)
	{
		at(block).call(clsr);
	}
}
