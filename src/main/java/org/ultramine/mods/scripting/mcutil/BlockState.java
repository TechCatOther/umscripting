package org.ultramine.mods.scripting.mcutil;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

public final class BlockState
{
	private final Block block;
	private final int meta;

	public BlockState(Block block, int meta)
	{
		if(meta != OreDictionary.WILDCARD_VALUE) //allow wildcard value
			GameUtil.checkBlockMeta(meta);
		this.block = block;
		this.meta = meta;
	}

	public BlockState(Block block)
	{
		this(block, 0);
	}

	public Block getType()
	{
		return block;
	}

	public int getMeta()
	{
		return meta;
	}

	public boolean isType(Block block)
	{
		return Block.isEqualTo(getType(), block);
	}

	public boolean isType(Block block, int meta)
	{
		return isType(block) && (this.meta == meta || meta == OreDictionary.WILDCARD_VALUE || this.meta == OreDictionary.WILDCARD_VALUE);
	}

	public boolean isType(BlockState other)
	{
		return isType(other.getType()) && (this.meta == other.meta || this.meta == OreDictionary.WILDCARD_VALUE || other.meta == OreDictionary.WILDCARD_VALUE);
	}

	public BlockState withMeta(int meta)
	{
		return new BlockState(block, meta);
	}

	@Override
	public int hashCode()
	{
		return Block.getIdFromBlock(block); //ignoring metadata in hash
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		BlockState other = (BlockState) o;
		return Objects.equals(this.block, other.block) && (this.meta == other.meta || this.meta == OreDictionary.WILDCARD_VALUE || other.meta == OreDictionary.WILDCARD_VALUE);
	}
}
