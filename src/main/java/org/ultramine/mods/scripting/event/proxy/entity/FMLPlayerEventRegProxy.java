package org.ultramine.mods.scripting.event.proxy.entity;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import groovy.lang.Closure;
import groovy.lang.Script;
import net.minecraft.entity.player.EntityPlayerMP;
import org.ultramine.mods.scripting.ScriptContainer;
import org.ultramine.mods.scripting.util.ScriptUtils;
import org.ultramine.mods.scripting.event.ClosureEventHandler;
import org.ultramine.mods.scripting.event.IPlayerAttachable;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;

public class FMLPlayerEventRegProxy implements IEventRegProxy, IPlayerAttachable
{
	private final Script script;
	private final EventPriority prior;
	private final Class<? extends PlayerEvent> eventType;
	private EntityPlayerMP player;

	public FMLPlayerEventRegProxy(Script script, EventPriority prior, Class<? extends PlayerEvent> eventType)
	{
		this.script = script;
		this.prior = prior;
		this.eventType = eventType;
	}

	@Override
	public void call(Closure<Void> clsr)
	{
		ScriptContainer cont = ScriptUtils.getContainer(script != null ? script : (Script)clsr.getThisObject());
		if(player == null)
			cont.registerEvent(eventType, prior, new ClosureEventHandler(clsr));
		else
			cont.registerEvent(eventType, prior, player, new PlayerSpecificClosureEventHandler(clsr, player.getEntityId()));
	}

	@Override
	public FMLPlayerEventRegProxy by(EntityPlayerMP player)
	{
		this.player = player;
		return this;
	}

	public void by(EntityPlayerMP player, Closure<Void> clsr)
	{
		by(player).call(clsr);
	}

	private static class PlayerSpecificClosureEventHandler extends ClosureEventHandler
	{
		private final int entityID;

		public PlayerSpecificClosureEventHandler(Closure<Void> handler, int entityID)
		{
			super(handler);
			this.entityID = entityID;
		}

		@Override
		public void invoke(Event event)
		{
			if(((PlayerEvent)event).player.getEntityId() == entityID)
				super.invoke(event);
		}
	}
}
