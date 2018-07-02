package org.ultramine.mods.scripting.event.proxy;

import cpw.mods.fml.common.eventhandler.Event;

@FunctionalInterface
public interface IEventFilter<T extends Event>
{
	boolean allow(T event);
}
