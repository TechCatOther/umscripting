package org.ultramine.mods.scripting.event;

import cpw.mods.fml.common.eventhandler.Event;

public interface IHoldedEventHandler
{
	void setHolder(EventHandlerHolder holder);

	void invoke(Event event);
}
