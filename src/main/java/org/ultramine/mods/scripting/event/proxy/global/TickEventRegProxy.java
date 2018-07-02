package org.ultramine.mods.scripting.event.proxy.global;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import groovy.lang.Closure;
import groovy.lang.Script;
import net.minecraft.server.MinecraftServer;
import org.ultramine.mods.scripting.event.proxy.FilterEventRegProxy;

public class TickEventRegProxy extends FilterEventRegProxy<ServerTickEvent>
{
	public TickEventRegProxy(Script script, EventPriority prior)
	{
		super(script, ServerTickEvent.class, prior);
		filter(e -> e.phase == ServerTickEvent.Phase.END);
	}

	public TickEventRegProxy skip(final int mod)
	{
		if(mod <= 0) throw new IllegalArgumentException(""+mod);
		filter(e -> MinecraftServer.getServer().getTickCounter() % mod == 0);
		return this;
	}

	public void skip(final int mod, Closure<Void> clsr)
	{
		skip(mod).call(clsr);
	}
}
