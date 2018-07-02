package org.ultramine.mods.scripting.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Script;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;

@FunctionalInterface
public interface IAliasedEventRegistry
{
	default IEventRegProxy getAt(String type)
	{
		return getAt(null, type, EventPriority.NORMAL);
	}

	default IEventRegProxy getAt(String type, EventPriority prior)
	{
		return getAt(null, type, prior);
	}

	IEventRegProxy getAt(Script script, String type, EventPriority prior);
}
