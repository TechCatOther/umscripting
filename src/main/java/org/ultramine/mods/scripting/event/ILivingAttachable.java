package org.ultramine.mods.scripting.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;

public interface ILivingAttachable extends IPlayerAttachable
{
	IEventRegProxy by(EntityLivingBase living);

	@Override
	default IEventRegProxy by(EntityPlayerMP player)
	{
		by((EntityLivingBase)player);
		return (IEventRegProxy)this;
	}
}
