package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.ultramine.mods.scripting.mcutil.GameUtil;
import org.ultramine.mods.scripting.util.ScriptUtils;
import org.ultramine.server.util.AsyncIOUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NBTMethodExtension
{
	public static Object asType(Map self, Class<?> cls)
	{
		if(cls == NBTTagCompound.class)
			return GameUtil.objectToNBT(self);
		return null;
	}

	public static NBTBase get(NBTTagCompound self, String name)
	{
		NBTBase nbt = self.getTag(name);
		if(nbt == null)
		{
			nbt = new NBTTagCompound();
			self.setTag(name, nbt);
		}
		return nbt;
	}

	public static NBTTagCompound set(NBTTagCompound self, String name, Object value)
	{
		self.setTag(name, GameUtil.objectToNBT(value));
		return self;
	}

	public static NBTBase getAt(NBTTagCompound self, String name)
	{
		return get(self, name);
	}

	public static void putAt(NBTTagCompound self, String name, Object value)
	{
		set(self, name, value);
	}

	public static Object propertyMissing(NBTTagCompound self, String name)
	{
		return get(self, name);
	}

	public static void propertyMissing(NBTTagCompound self, String name, Object value)
	{
		set(self, name, value);
	}

	public static boolean contains(NBTTagCompound self, String key)
	{
		return self.hasKey(key);
	}

	public static int asInt(NBTBase self)
	{
		if(self instanceof NBTBase.NBTPrimitive)
			return ((NBTBase.NBTPrimitive)self).func_150287_d();
		return 0;
	}

	public static long asLong(NBTBase self)
	{
		if(self instanceof NBTBase.NBTPrimitive)
			return ((NBTBase.NBTPrimitive)self).func_150291_c();
		return 0;
	}

	public static float asFloat(NBTBase self)
	{
		if(self instanceof NBTBase.NBTPrimitive)
			return ((NBTBase.NBTPrimitive)self).func_150288_h();
		return 0;
	}

	public static double asDouble(NBTBase self)
	{
		if(self instanceof NBTBase.NBTPrimitive)
			return ((NBTBase.NBTPrimitive)self).func_150286_g();
		return 0;
	}

	public static String asString(NBTBase self)
	{
		if(self instanceof NBTTagString)
			return ((NBTTagString)self).func_150285_a_();
		return "";
	}

	public static byte[] asByteArray(NBTBase self)
	{
		if(self instanceof NBTTagByteArray)
			return ((NBTTagByteArray)self).func_150292_c();
		return new byte[0];
	}

	public static int[] asIntArray(NBTBase self)
	{
		if(self instanceof NBTTagIntArray)
			return ((NBTTagIntArray)self).func_150302_c();
		return new int[0];
	}

	public static List<?> asList(NBTBase self)
	{
		if(self instanceof NBTTagList)
			return ScriptUtils.getNBTList((NBTTagList)self);
		return Collections.emptyList();
	}

	public static NBTTagCompound write(NBTTagCompound self, File file)
	{
		AsyncIOUtils.safeWriteNBT(file, self);
		return self;
	}
}
