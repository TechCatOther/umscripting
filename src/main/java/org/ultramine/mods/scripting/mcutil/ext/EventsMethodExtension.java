package org.ultramine.mods.scripting.mcutil.ext;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.ultramine.mods.scripting.mcutil.WorldBlock;

public class EventsMethodExtension
{
	public static void cancel(Event e)
	{
		if(e.isCancelable())
			e.setCanceled(true);
		else if(e.hasResult())
			e.setResult(Event.Result.DENY);
		else
			throw new IllegalStateException("Tried to cancel non cancellable event");
	}

	public static EntityPlayerMP getPlayer(PlayerEvent self)
	{
		return (EntityPlayerMP)self.entityPlayer;
	}

	public static EntityPlayerMP getPlayer(EntityEvent self)
	{
		return (EntityPlayerMP)self.entity;
	}

	public static WorldBlock getBlock(PlayerInteractEvent self)
	{
		return new WorldBlock((WorldServer)self.world, self.x, self.y, self.z);
	}

	public static WorldBlock blockAt(PlayerInteractEvent self, int x, int y, int z)
	{
		return new WorldBlock((WorldServer)self.world, x, y, z);
	}

	public static WorldBlock getWblock(BlockEvent self)
	{
		return new WorldBlock((WorldServer)self.world, self.x, self.y, self.z);
	}
}
