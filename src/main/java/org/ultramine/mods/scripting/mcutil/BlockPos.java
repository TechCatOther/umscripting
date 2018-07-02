package org.ultramine.mods.scripting.mcutil;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPos implements IBlockPos
{
	public static final BlockPos EMPTY = new BlockPos();

	public final int x;
	public final int y;
	public final int z;

	public BlockPos(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockPos()
	{
		this(0, 0, 0);
	}

	public BlockPos(double x, double y, double z)
	{
		this.x = MathHelper.floor_double(x);
		this.y = MathHelper.floor_double(y);
		this.z = MathHelper.floor_double(z);
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getY()
	{
		return y;
	}

	@Override
	public int getZ()
	{
		return z;
	}

	public BlockPos step(ForgeDirection d)
	{
		return new BlockPos(x + d.offsetX, y + d.offsetY, z + d.offsetZ);
	}

	public BlockPos stepBack(ForgeDirection d)
	{
		return new BlockPos(x - d.offsetX, y - d.offsetY, z - d.offsetZ);
	}

	@Override
	public boolean isInAABB(IBlockPos min, IBlockPos max)
	{
		return x >= min.getX() && x <= max.getX() && y >= min.getY() && y <= max.getY() && z >= min.getZ() && z <= max.getZ();
	}

	public static BlockPos fromChunkPosition(ChunkPosition block)
	{
		return new BlockPos(block.chunkPosX, block.chunkPosY, block.chunkPosZ);
	}

	public static BlockPos fromTileEntity(TileEntity block)
	{
		return new BlockPos(block.xCoord, block.yCoord, block.zCoord);
	}

	public String toString()
	{
		return "BlockPos(" + x + ", " + y + ", " + z + ")";
	}

	public boolean equals(BlockPos v)
	{
		return x == v.x && y == v.y && z == v.z;
	}
}
