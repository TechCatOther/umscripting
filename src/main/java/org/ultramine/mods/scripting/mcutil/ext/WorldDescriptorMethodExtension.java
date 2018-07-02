package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.ultramine.mods.scripting.mcutil.WorldBlock;
import org.ultramine.mods.scripting.mcutil.WorldSelection;
import org.ultramine.server.world.WorldDescriptor;

public class WorldDescriptorMethodExtension
{
	public static Object asType(WorldDescriptor self, Class<?> cls)
	{
		if(cls == World.class || cls == WorldServer.class)
			return self.getOrLoadWorld();
		return null;
	}

	public static Object methodMissing(WorldDescriptor self, String name, Object args)
	{
		return InvokerHelper.invokeMethod(self.getOrLoadWorld(), name, args);
	}

	public static Object propertyMissing(WorldDescriptor self, String name)
	{
		return InvokerHelper.getProperty(self.getOrLoadWorld(), name);
	}

	public static void propertyMissing(WorldDescriptor self, String name, Object value)
	{
		InvokerHelper.setProperty(self.getOrLoadWorld(), name, value);
	}

	public static boolean isLoaded(WorldDescriptor self)
	{
		return self.getState().isLoaded();
	}

	public static WorldBlock blockAt(WorldDescriptor self, int x, int y, int z)
	{
		return WorldMethodExtension.blockAt(self.getOrLoadWorld(), x, y, z);
	}

	public static WorldSelection blockRect(WorldDescriptor self, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		return WorldMethodExtension.blockRect(self.getOrLoadWorld(), x1, y1, z1, x2, y2, z2);
	}
}
