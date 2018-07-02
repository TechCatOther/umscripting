package org.ultramine.mods.scripting.event.proxy;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import groovy.lang.Script;
import org.ultramine.mods.scripting.event.ClosureEventHandler;

import java.util.ArrayList;
import java.util.List;

public class FilterEventRegProxy<T extends Event> extends AbstractEventRegProxy
{
	private List<IEventFilter<T>> filres = new ArrayList<>(4);

	public FilterEventRegProxy(Script script, Class<? extends T> eventType, EventPriority prior)
	{
		super(script, eventType, prior);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void call(Closure<Void> clsr)
	{
		if(filres.size() == 0)
		{
			register(clsr, new ClosureEventHandler(clsr));
		}
		else
		{
			final IEventFilter[] filterArr = filres.toArray(new IEventFilter[filres.size()]);
			register(clsr, new FilteredClosureEventHandler(clsr, filterArr));
		}
	}

	public void filter(IEventFilter<T> filter)
	{
		filres.add(filter);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static class FilteredClosureEventHandler extends ClosureEventHandler
	{
		private final IEventFilter[] filterArr;

		public FilteredClosureEventHandler(Closure<Void> handler, IEventFilter[] filterArr)
		{
			super(handler);
			this.filterArr = filterArr;
		}

		@Override
		public void invoke(Event event)
		{
			for(IEventFilter filter : filterArr)
				if(!filter.allow(event))
					return;
			super.invoke(event);
		}
	}
}
