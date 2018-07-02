package org.ultramine.mods.scripting.event.proxy;

import groovy.lang.Closure;

public interface IEventRegProxy
{
	void call(Closure<Void> clsr);
}
