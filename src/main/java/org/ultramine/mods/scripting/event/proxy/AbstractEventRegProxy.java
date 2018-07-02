package org.ultramine.mods.scripting.event.proxy;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import groovy.lang.Script;
import net.minecraft.entity.Entity;
import org.ultramine.mods.scripting.ScriptContainer;
import org.ultramine.mods.scripting.util.ScriptUtils;
import org.ultramine.mods.scripting.event.IHoldedEventHandler;

public abstract class AbstractEventRegProxy implements IEventRegProxy
{
	protected final Script script;
	protected final Class<? extends Event> eventType;
	protected final EventPriority prior;
	private Entity attachedToEntity;

	public AbstractEventRegProxy(Script script, Class<? extends Event> eventType, EventPriority prior)
	{
		this.script = script;
		this.eventType = eventType;
		this.prior = prior;
	}

	protected void register(Closure<Void> clsr, IHoldedEventHandler handler)
	{
		ScriptContainer cont = ScriptUtils.getContainer(script != null ? script : (Script) clsr.getThisObject());
		if(attachedToEntity != null)
			cont.registerEvent(eventType, prior, attachedToEntity, handler);
		else
			cont.registerEvent(eventType, prior, handler);
	}

	protected void attachToEntity(Entity entity)
	{
		if(this.attachedToEntity != null)
			throw new IllegalStateException("Entity already attached");
		this.attachedToEntity = entity;
	}
}
