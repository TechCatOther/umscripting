package org.ultramine.mods.scripting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import org.ultramine.mods.scripting.mcutil.internal.TempPlayerData;
import org.ultramine.server.data.player.PlayerData;

public class GlobalEventHandler
{
	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e)
	{
		EntityPlayerMP player = ((EntityPlayerMP)e.player);
		PlayerData data = player.getData();
		if(data != null)
			data.get(TempPlayerData.class).onDisconnect(player);

		for(ScriptContainer cont : UMScripting.instance().getLoader().getAllContainers())
			cont.onPlayerLogout(player);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
	{
		((EntityPlayerMP)e.player).getData().get(TempPlayerData.class).unstashData((EntityPlayerMP) e.player);
	}
}
