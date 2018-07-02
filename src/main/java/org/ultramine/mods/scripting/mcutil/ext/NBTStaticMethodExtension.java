package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class NBTStaticMethodExtension
{
	public static NBTTagCompound read(NBTTagCompound unused, File file)
	{
		try
		{
			return CompressedStreamTools.readCompressed(new FileInputStream(file));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
