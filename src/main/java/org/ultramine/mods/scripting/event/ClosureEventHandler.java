package org.ultramine.mods.scripting.event;

import cpw.mods.fml.common.eventhandler.Event;
import groovy.lang.Closure;

public class ClosureEventHandler implements IHoldedEventHandler
{
	private final Closure<Void> handler;
	private EventHandlerHolder holder;

	public Event e;

	public ClosureEventHandler(Closure<Void> handler)
	{
		this.handler = handler;
		handler.setResolveStrategy(Closure.DELEGATE_FIRST);
		handler.setDelegate(this);
	}

	@Override
	public void setHolder(EventHandlerHolder holder)
	{
		if(this.holder != null)
			throw new IllegalStateException("holder is already set");
		this.holder = holder;
	}

	public void unregister()
	{
		holder.unregister();
	}

	@Override
	public void invoke(Event event)
	{
		e = event;
		try
		{
			handler.call();
		}
		finally
		{
			e = null;
		}
	}
}
