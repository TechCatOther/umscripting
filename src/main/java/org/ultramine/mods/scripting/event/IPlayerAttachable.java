package org.ultramine.mods.scripting.event;

import net.minecraft.entity.player.EntityPlayerMP;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;

public interface IPlayerAttachable
{
	IEventRegProxy by(EntityPlayerMP player);
}
