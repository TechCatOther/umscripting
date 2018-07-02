package org.ultramine.mods.scripting.event.proxy.global;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import org.ultramine.mods.scripting.ScriptContainer;
import org.ultramine.mods.scripting.event.ClosureEventHandler;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;

public class GlobalRegistrationProxy implements IEventRegProxy
{
	private final ScriptContainer container;
	private final Class<? extends Event> cls;
	private final EventPriority prior;

	public GlobalRegistrationProxy(ScriptContainer container, Class<? extends Event> cls, EventPriority prior)
	{
		this.container = container;
		this.cls = cls;
		this.prior = prior;
	}

	@Override
	public void call(Closure<Void> clsr)
	{
		container.registerEvent(cls, prior, new ClosureEventHandler(clsr));
	}
}
