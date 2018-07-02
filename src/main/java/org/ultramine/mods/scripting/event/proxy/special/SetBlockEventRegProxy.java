package org.ultramine.mods.scripting.event.proxy.special;

import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import groovy.lang.Script;
import org.ultramine.mods.scripting.event.IBlockTypesAttachable;
import org.ultramine.mods.scripting.event.IWorldBlockAttachable;
import org.ultramine.mods.scripting.event.proxy.FilterEventRegProxy;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;
import org.ultramine.mods.scripting.mcutil.BlockState;
import org.ultramine.mods.scripting.mcutil.BlockTypes;
import org.ultramine.mods.scripting.mcutil.WorldBlock;
import org.ultramine.server.event.SetBlockEvent;

public class SetBlockEventRegProxy extends FilterEventRegProxy<SetBlockEvent> implements IBlockTypesAttachable, IWorldBlockAttachable
{
	public SetBlockEventRegProxy(Script script, EventPriority prior)
	{
		super(script, SetBlockEvent.class, prior);
	}

	@Override
	public IEventRegProxy block(BlockTypes block)
	{
		filter(e -> block.isType(e.newBlock, e.newMeta) || block.isType(e.world.getBlock(e.x, e.y, e.z), e.world.getBlockMetadata(e.x, e.y, e.z)));
		return this;
	}

	public IEventRegProxy block(BlockState block)
	{
		filter(e -> block.isType(e.newBlock, e.newMeta) || block.isType(e.world.getBlock(e.x, e.y, e.z), e.world.getBlockMetadata(e.x, e.y, e.z)));
		return this;
	}

	@Override
	public IEventRegProxy at(WorldBlock block)
	{
		filter(e -> e.world.provider.dimensionId == block.getWorld().getDimension() && e.x == block.x && e.y == block.y && e.z == block.z);
		return this;
	}

	public void at(WorldBlock block, Closure<Void> clsr)
	{
		at(block).call(clsr);
	}

	public void block(BlockTypes block, Closure<Void> clsr)
	{
		block(block).call(clsr);
	}

	public void block(BlockState block, Closure<Void> clsr)
	{
		block(block).call(clsr);
	}
}
