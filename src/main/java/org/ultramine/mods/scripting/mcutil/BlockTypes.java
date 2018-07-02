package org.ultramine.mods.scripting.mcutil;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import org.ultramine.mods.scripting.event.EventAliasRegistry;
import org.ultramine.mods.scripting.event.IAliasedEventRegistry;
import org.ultramine.mods.scripting.event.IEventSource;

import java.util.List;

public class BlockTypes implements IEventSource
{
	private BlockState[] blocks;

	public BlockTypes(BlockState... blocks)
	{
		this.blocks = blocks;
	}

	public boolean contains(Block block)
	{
		for(BlockState b : blocks)
			if(Block.isEqualTo(b.getType(), block))
				return true;
		return false;
	}

	public boolean contains(Block block, int meta)
	{
		for(BlockState b : blocks)
			if(b.isType(block, meta))
				return true;
		return false;
	}

	public boolean contains(BlockState block)
	{
		for(BlockState b : blocks)
			if(b.isType(block))
				return true;
		return false;
	}

	public boolean isType(Block block)
	{
		return contains(block);
	}

	public boolean isType(Block block, int meta)
	{
		return contains(block, meta);
	}

	public boolean isType(BlockState block)
	{
		return contains(block);
	}

	public boolean isType(WorldBlock block)
	{
		return contains(block.getType());
	}

	@Override
	public IAliasedEventRegistry getOn()
	{
		return (script, type, prior) -> EventAliasRegistry.createBlockTypesProxy(script, type, prior, this);
	}

	public static BlockTypes of(Block block)
	{
		return new BlockTypes(new BlockState(block, OreDictionary.WILDCARD_VALUE));
	}

	public static BlockTypes of(Block... blocks)
	{
		BlockState[] states = new BlockState[blocks.length];
		for(int i = 0; i < blocks.length; i++)
		{
			states[i] = new BlockState(blocks[i], OreDictionary.WILDCARD_VALUE);
		}
		return new BlockTypes(states);
	}

	@SuppressWarnings("rawtypes")
	public static BlockTypes of(Object... objs)
	{
		BlockState[] blocks = new BlockState[objs.length];
		for(int i = 0; i < objs.length; i++)
		{
			Object obj = objs[i];
			if(obj instanceof List)
				blocks[i] = asBlock(((List)obj).get(0)).withMeta((Integer)((List)obj).get(1));
			else
				blocks[i] = asBlock(obj);
		}
		return new BlockTypes(blocks);
	}

	private static BlockState asBlock(Object obj)
	{
		if(obj instanceof BlockState)
			return (BlockState)obj;
		else if(obj instanceof Block)
			return new BlockState((Block)obj);
		else if(obj instanceof Integer)
			return new BlockState(GameUtil.getBlock((Integer)obj));
		else
			return new BlockState(GameUtil.getBlock(obj.toString()));
	}
}
