package org.ultramine.mods.scripting.event.proxy;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import groovy.lang.Script;
import org.ultramine.mods.scripting.event.ClosureEventHandler;

public class DefaultEventRegProxy extends AbstractEventRegProxy
{
	public DefaultEventRegProxy(Script script, EventPriority prior, Class<? extends Event> eventType)
	{
		super(script, eventType, prior);
	}

	@Override
	public void call(Closure<Void> clsr)
	{
		register(clsr, new ClosureEventHandler(clsr));
	}
}
