package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import org.ultramine.server.util.InventoryUtil;

import java.util.AbstractList;
import java.util.List;

public class IInventoryMethodExtension
{
	public static int size(IInventory self)
	{
		return self.getSizeInventory();
	}

	public static ItemStack get(IInventory self, int slot)
	{
		return self.getStackInSlot(slot);
	}

	public static ItemStack getAt(IInventory self, int slot)
	{
		return self.getStackInSlot(slot);
	}

	public static void set(IInventory self, int slot, ItemStack stack)
	{
		self.setInventorySlotContents(slot, stack.copy());
	}

	public static void putAt(IInventory self, int slot, ItemStack stack)
	{
		set(self, slot, stack);
	}

	public static List<ItemStack> add(IInventory self, ItemStack... stacks)
	{
		return InventoryUtil.addItem(self, stacks);
	}

	public static ItemStack add(IInventory self, ItemStack stack)
	{
		return InventoryUtil.addItem(self, stack);
	}

	public static ItemStack add(IInventory self, Item item)
	{
		return InventoryUtil.addItem(self, new ItemStack(item));
	}

	public static ItemStack add(IInventory self, Block block)
	{
		return InventoryUtil.addItem(self, new ItemStack(block));
	}

	public static ItemStack addForce(IInventory self, ItemStack stack)
	{
		return InventoryUtil.addItem(self, stack, false, true);
	}

	public static List<ItemStack> remove(IInventory self, ItemStack... stacks)
	{
		return InventoryUtil.removeItem(self, stacks);
	}

	public static ItemStack remove(IInventory self, ItemStack stack)
	{
		return InventoryUtil.removeItem(self, stack);
	}

	public static ItemStack remove(IInventory self, Item item)
	{
		return InventoryUtil.removeItem(self, new ItemStack(item));
	}

	public static ItemStack remove(IInventory self, Block block)
	{
		return InventoryUtil.removeItem(self, new ItemStack(block));
	}

	public static boolean containsType(IInventory self, ItemStack stack)
	{
		return InventoryUtil.contains(self, stack);
	}

	public static boolean containsType(IInventory self, Item item)
	{
		return InventoryUtil.contains(self, new ItemStack(item));
	}

	public static boolean containsType(IInventory self, Block block)
	{
		return InventoryUtil.contains(self, new ItemStack(block));
	}

	public static boolean containsAmount(IInventory self, ItemStack stack)
	{
		return InventoryUtil.containsAmount(self, stack, stack.stackSize);
	}

	public static boolean containsAmount(IInventory self, ItemStack stack, int size)
	{
		return InventoryUtil.containsAmount(self, stack, size);
	}

	public static int first(IInventory self, ItemStack stack)
	{
		return InventoryUtil.first(self, stack);
	}

	public static int firstPartial(IInventory self, ItemStack stack)
	{
		return InventoryUtil.firstPartial(self, stack);
	}

	public static int firstEmpty(IInventory self)
	{
		return InventoryUtil.firstEmpty(self);
	}

	public static void dropAll(IInventory self, WorldServer world, int x, int y, int z)
	{
		for(int i = 0; i < self.getSizeInventory(); i++)
		{
			ItemStack is = self.getStackInSlotOnClosing(i);
			if(is != null)
				InventoryUtil.dropItem(world, x, y, z, is);
		}
	}

	public static void clear(IInventory self)
	{
		for(int i = 0, s = self.getSizeInventory(); i < s; i++)
		{
			ItemStack is = self.getStackInSlot(i);
			if(is != null)
				self.setInventorySlotContents(i, null);
		}
	}

	public static List<ItemStack> asList(final IInventory self)
	{
		return new AbstractList<ItemStack>()
		{
			@Override
			public ItemStack get(int index)
			{
				return self.getStackInSlot(index);
			}

			@Override
			public int size()
			{
				return self.getSizeInventory();
			}

			public ItemStack set(int index, ItemStack element)
			{
				ItemStack ret = self.getStackInSlot(index);
				self.setInventorySlotContents(index, element);
				return ret;
			}
		};
	}
}
