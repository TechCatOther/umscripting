package org.ultramine.mods.scripting.event;

import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;
import org.ultramine.mods.scripting.mcutil.WorldBlock;

public interface IWorldBlockAttachable
{
	IEventRegProxy at(WorldBlock block);
}
