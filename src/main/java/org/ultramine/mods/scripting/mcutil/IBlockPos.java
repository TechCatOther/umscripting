package org.ultramine.mods.scripting.mcutil;

public interface IBlockPos
{
	int getX();

	int getY();

	int getZ();

	boolean isInAABB(IBlockPos min, IBlockPos max);
}
