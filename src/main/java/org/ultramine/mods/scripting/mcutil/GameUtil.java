package org.ultramine.mods.scripting.mcutil;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.scripting.mcutil.exception.IllegalMetadataException;
import org.ultramine.mods.scripting.mcutil.exception.UnknownBlockException;
import org.ultramine.mods.scripting.mcutil.exception.UnknownItemException;

import java.util.Map;

public class GameUtil
{
	private static final Logger log = LogManager.getLogger();

	public static Block getBlock(String id)
	{
		Block block = Block.getBlockFromName(id);
		if(block == null)
			throw new UnknownBlockException(id);
		return block;
	}

	public static Block getBlock(int id)
	{
		Block block = Block.getBlockById(id);
		if(block == null)
			throw new UnknownBlockException(id);
		return block;
	}

	public static Block getBlock(Item item)
	{
		Block block = Block.getBlockFromItem(item);
		if(block == null)
			throw new UnknownBlockException(Item.itemRegistry.getNameForObject(item));
		return block;
	}

	public static Item getItem(String id)
	{
		Item item = (Item)Item.itemRegistry.getObject(id);
		if(item == null)
			throw new UnknownItemException(id);
		return item;
	}

	public static Item getItem(int id)
	{
		Item item = Item.getItemById(id);
		if(item == null)
			throw new UnknownItemException(id);
		return item;
	}

	public static NBTTagCompound parseNBT(String nbtString)
	{
		if(!Strings.isNullOrEmpty(nbtString))
		{
			NBTBase nbttag = null;
			try
			{
				nbttag = JsonToNBT.func_150315_a(nbtString);
			}
			catch (NBTException e)
			{
				log.warn("Encountered an exception parsing ItemStack NBT string {}", nbtString, e);
				throw Throwables.propagate(e);
			}
			if(!(nbttag instanceof NBTTagCompound))
			{
				log.warn("Unexpected NBT string - multiple values {}", nbtString);
				throw new RuntimeException("Invalid NBT JSON");
			}
			else
			{
				return (NBTTagCompound) nbttag;
			}
		}

		return null;
	}

	public static NBTBase objectToNBT(Object value)
	{
		if(value == null)
		{
			throw new IllegalArgumentException("Null NBT value is not supported");
		}
		else if(value instanceof NBTBase)
		{
			return (NBTBase)value;
		}
		else if(value instanceof Byte)
		{
			return new NBTTagByte((byte)value);
		}
		else if(value instanceof Short)
		{
			return new NBTTagShort((short)value);
		}
		else if(value instanceof Integer)
		{
			return new NBTTagInt((int)value);
		}
		else if(value instanceof Long)
		{
			return new NBTTagLong((long)value);
		}
		else if(value instanceof Float)
		{
			return new NBTTagFloat((float)value);
		}
		else if(value instanceof Double)
		{
			return new NBTTagDouble((double)value);
		}
		else if(value instanceof byte[])
		{
			return new NBTTagByteArray((byte[])value);
		}
		else if(value instanceof int[])
		{
			return new NBTTagIntArray((int[])value);
		}
		else if(value instanceof CharSequence)
		{
			return new NBTTagString(value.toString());
		}
		else if(value instanceof Iterable)
		{
			NBTTagList dst = new NBTTagList();
			for(Object o : (Iterable<?>)value)
				dst.appendTag(objectToNBT(o));
			return dst;
		}
		else if(value instanceof Map)
		{
			Map<?, ?> src = (Map<?, ?>)value;
			NBTTagCompound dst = new NBTTagCompound();
			for(Map.Entry<?, ?> ent : src.entrySet())
				dst.setTag(ent.getKey().toString(), objectToNBT(ent.getValue()));
			return dst;
		}
		else
		{
			throw new IllegalArgumentException("Unsupported object type: " + value.getClass().getName() + ". Object: " + value);
		}
	}

	public static void checkBlockMeta(int meta) throws IllegalMetadataException
	{
		if(meta > 15 || meta < 0)
			throw new IllegalMetadataException("Block meta cat't be more then 15 or less then 0. Given: " + meta, meta);
	}
}
