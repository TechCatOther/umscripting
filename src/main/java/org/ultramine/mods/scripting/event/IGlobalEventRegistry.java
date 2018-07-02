package org.ultramine.mods.scripting.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;

public interface IGlobalEventRegistry
{
	default IEventRegProxy getAt(Class<? extends Event> cls)
	{
		return getAt(cls, EventPriority.NORMAL);
	}

	IEventRegProxy getAt(Class<? extends Event> cls, EventPriority prior);
}
