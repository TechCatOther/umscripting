package org.ultramine.mods.scripting.mcutil;

import net.minecraft.block.Block;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import org.ultramine.server.util.TrigMath;
import org.ultramine.server.util.WarpLocation;
import org.ultramine.server.world.WorldDescriptor;

public class WorldSelection
{
	private final WorldDescriptor desc;
	private final BlockPos min;
	private final BlockPos max;

	public WorldSelection(WorldDescriptor desc, BlockPos min, BlockPos max)
	{
		this.desc = desc;
		this.min = min;
		this.max = max;
	}

	public WorldSelection(WorldDescriptor desc, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		this(desc, new BlockPos(x1, y1, z1), new BlockPos(x2, y2, z2));
	}

	public BlockPos getMin()
	{
		return min;
	}

	public BlockPos getMax()
	{
		return max;
	}

	public int getLenX()
	{
		return max.x - min.x + 1;
	}

	public int getLenY()
	{
		return max.y - min.y + 1;
	}

	public int getLenZ()
	{
		return max.z - min.z + 1;
	}

	public int getLen(ForgeDirection dir)
	{
		switch(dir)
		{
			case DOWN:	return getLenY();
			case NORTH:	return getLenZ();
			case WEST:	return getLenX();
			case UP:	return getLenY();
			case SOUTH:	return getLenZ();
			case EAST:	return getLenX();
			case UNKNOWN: throw new IllegalArgumentException("UNKNOWN direction");
		}

		return 0;
	}

	public WorldSelection expand(ForgeDirection dir, int amount)
	{
		switch(dir)
		{
			case DOWN:	return new WorldSelection(desc, new BlockPos(min.x, min.y - amount, min.z), max);
			case NORTH:	return new WorldSelection(desc, new BlockPos(min.x, min.y, min.z - amount), max);
			case WEST:	return new WorldSelection(desc, new BlockPos(min.x - amount, min.y, min.z), max);
			case UP:	return new WorldSelection(desc, min, new BlockPos(max.x, max.y + amount, max.z));
			case SOUTH:	return new WorldSelection(desc, min, new BlockPos(max.x, max.y, max.z + amount));
			case EAST:	return new WorldSelection(desc, min, new BlockPos(max.x + amount, max.y, max.z));
			case UNKNOWN: throw new IllegalArgumentException("UNKNOWN direction");
		}

		return this;
	}

	public WorldSelection compress(ForgeDirection dir, int amount)
	{
		return expand(dir, -amount);
	}

	public WorldSelection expandAll(int amount)
	{
		return new WorldSelection(desc, new BlockPos(min.x-amount, min.y-amount, min.z-amount), new BlockPos(max.x+amount, max.y+amount, max.z+amount));
	}

	public boolean isIntersects(WorldSelection other)
	{
		return isIntersects(min, max, other.min, other.max);
	}

	public boolean contains(IBlockPos point)
	{
		return point.isInAABB(min, max);
	}

	public boolean contains(WorldSelection rect)
	{
		return isBoxInBox(rect.min, rect.max, min, max);
	}

	public BlockPos getCornerPos(int index)
	{
		switch(index)
		{
			case 0:
				return new BlockPos(min.x, min.y, min.z);
			case 1:
				return new BlockPos(max.x, min.y, min.z);
			case 2:
				return new BlockPos(max.x, min.y, max.z);
			case 3:
				return new BlockPos(min.x, min.y, max.z);
			case 4:
				return new BlockPos(min.x, max.y, min.z);
			case 5:
				return new BlockPos(max.x, max.y, min.z);
			case 6:
				return new BlockPos(max.x, max.y, max.z);
			case 7:
				return new BlockPos(min.x, max.y, max.z);
		}

		return null;
	}

	public WarpLocation getCornerLoc(int index)
	{
		BlockPos pos = getCornerPos(index);
		BlockPos opp = getCornerPos((index+2)%4);
		return new WarpLocation(desc.getDimension(), pos.x+0.5d, pos.y, pos.z+0.5d, (float)Math.toDegrees(TrigMath.atan2(pos.x-opp.x, opp.z - pos.z)), 0);
	}

	public WorldBlock blockAt(int x, int y, int z)
	{
		if(min.x+x > max.x || min.y+y > max.y || min.z+z > max.z)
			throw new IndexOutOfBoundsException("("+x+", "+y+", "+z+") out of box "+toString());

		return new WorldBlock(desc, min.x+x, min.y+y, min.z+z);
	}

	public void setType(Block block)
	{
		setType(block, 0);
	}

	public void setType(Block block, int meta)
	{
		GameUtil.checkBlockMeta(meta);
		WorldServer world = desc.getOrLoadWorld();
		for(int x = min.x; x <= max.x; x++)
			for(int y = min.y; y <= max.y; y++)
				for(int z = min.z; z <= max.z; z++)
					world.setBlockSilently(x, y, z, block, meta, 3);
	}

	public void setType(BlockState state)
	{
		GameUtil.checkBlockMeta(state.getMeta()); //exclude wildcard value
		setType(state.getType(), state.getMeta());
	}

	public void setType(String type)
	{
		setType(type, 0);
	}

	public void setType(String type, int meta)
	{
		GameUtil.checkBlockMeta(meta);
		setType(GameUtil.getBlock(type), meta);
	}

	public void replaceType(Block oldBlock, int oldMeta, Block newBlock, int newMeta)
	{
		GameUtil.checkBlockMeta(oldMeta);
		GameUtil.checkBlockMeta(newMeta);
		WorldServer world = desc.getOrLoadWorld();
		for(int x = min.x; x < max.x; x++)
			for(int y = min.y; y < max.y; y++)
				for(int z = min.z; z < max.z; z++)
					if(world.getBlock(x, y, z) == oldBlock && world.getBlockMetadata(x, y, z) == oldMeta)
						world.setBlock(x, y, z, newBlock, newMeta, 3);
	}

	public void replaceType(BlockState oldState, BlockState newState)
	{
		replaceType(oldState.getType(), oldState.getMeta(), newState.getType(), newState.getMeta());
	}

	public String toString()
	{
		return "WorldSelection{Dim["+desc.getDimension()+"], min("+min.x + ", "+min.y+", "+min.z+"), max("+max.x + ", "+max.y+", "+max.z+")}";
	}

	public static boolean isIntersects(BlockPos min1, BlockPos max1, BlockPos min2, BlockPos max2)
	{
		return
				((min1.x <= min2.x && min2.x <= max1.x) || (min1.x <= max2.x && max2.x <= max1.x) || (min1.x < min2.x && max1.x > max2.x) || (min2.x < min1.x && max2.x > max1.x)) &&
				((min1.y <= min2.y && min2.y <= max1.y) || (min1.y <= max2.y && max2.y <= max1.y) || (min1.y < min2.y && max1.y > max2.y) || (min2.y < min1.y && max2.y > max1.y)) &&
				((min1.z <= min2.z && min2.z <= max1.z) || (min1.z <= max2.z && max2.z <= max1.z) || (min1.z < min2.z && max1.z > max2.z) || (min2.z < min1.z && max2.z > max1.z));
	}

	public static boolean isBoxInBox(BlockPos min1, BlockPos max1, BlockPos min2, BlockPos max2)
	{
		return
				min1.x >= min2.x && max1.x <= max2.x &&
				min1.y >= min2.y && max1.y <= max2.y &&
				min1.z >= min2.z && max1.z <= max2.z;
	}
}
