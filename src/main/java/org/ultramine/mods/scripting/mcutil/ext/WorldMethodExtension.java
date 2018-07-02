package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import org.ultramine.mods.scripting.mcutil.WorldSelection;
import org.ultramine.mods.scripting.mcutil.WorldBlock;
import org.ultramine.server.world.WorldDescriptor;

public class WorldMethodExtension
{
	public static Object asType(WorldServer self, Class<?> cls)
	{
		if(cls == WorldDescriptor.class)
			return self.func_73046_m().getMultiWorld().getDescFromWorld(self);
		return null;
	}

	public static WorldBlock blockAt(WorldServer self, int x, int y, int z)
	{
		return new WorldBlock(self, x, y, z);
	}
	public static WorldBlock blockAt(WorldServer self, double x, double y, double z)
	{
		return new WorldBlock(self, MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z));
	}

	public static WorldSelection blockRect(WorldServer self, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		return new WorldSelection(self.func_73046_m().getMultiWorld().getDescFromWorld(self), x1, y1, z1, x2, y2, z2);
	}

	public static int getDimension(WorldServer self)
	{
		return self.provider.dimensionId;
	}
}
