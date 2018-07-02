package org.ultramine.mods.scripting.event.proxy.entity;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Script;
import net.minecraft.entity.player.EntityPlayerMP;
import org.ultramine.mods.scripting.event.EventAliasRegistry;
import org.ultramine.mods.scripting.event.IAliasedEventRegistry;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;

public class PlayerEventRegistry implements IAliasedEventRegistry
{
	private final EntityPlayerMP player;

	public PlayerEventRegistry(EntityPlayerMP player)
	{
		this.player = player;
	}

	@Override
	public IEventRegProxy getAt(Script script, String type, EventPriority prior)
	{
		return EventAliasRegistry.createPlayerProxy(script, type, prior, player);
	}
}
