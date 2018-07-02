package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import org.ultramine.mods.scripting.mcutil.GameUtil;
import org.ultramine.mods.scripting.mcutil.WorldBlock;
import org.ultramine.server.world.WorldDescriptor;

public class ItemStackMethodExtension
{
	public static ItemStack withSize(ItemStack self, int size)
	{
		ItemStack copy = self.copy();
		copy.stackSize = size;
		return copy;
	}

	public static ItemStack withMeta(ItemStack self, int meta)
	{
		ItemStack copy = self.copy();
		copy.setItemDamage(meta);
		return copy;
	}

	public static ItemStack withNBT(ItemStack self, NBTTagCompound nbt)
	{
		ItemStack copy = self.copy();
		copy.stackTagCompound = nbt;
		return copy;
	}

	public static ItemStack withNBT(ItemStack self, String nbtString)
	{
		ItemStack copy = self.copy();
		copy.stackTagCompound = GameUtil.parseNBT(nbtString);
		return copy;
	}

	public static void drop(ItemStack self, WorldServer world, int x, int y, int z)
	{
		EntityItem ei = new EntityItem(world, x+0.5d, y, z+0.5d, self.copy());
		world.spawnEntityInWorld(ei);
	}

	public static void drop(ItemStack self, WorldServer world, double x, double y, double z)
	{
		EntityItem ei = new EntityItem(world, x, y, z, self.copy());
		world.spawnEntityInWorld(ei);
	}

	public static void drop(ItemStack self, WorldDescriptor desc, int x, int y, int z)
	{
		drop(self, desc.getOrLoadWorld(), x, y, z);
	}

	public static void drop(ItemStack self, WorldDescriptor desc, double x, double y, double z)
	{
		drop(self, desc.getOrLoadWorld(), x, y, z);
	}

	public static void drop(ItemStack self, WorldBlock block)
	{
		drop(self, block.getWorldHandle(), block.x, block.y, block.z);
	}
}
