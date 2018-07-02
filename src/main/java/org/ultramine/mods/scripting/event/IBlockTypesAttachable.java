package org.ultramine.mods.scripting.event;

import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;
import org.ultramine.mods.scripting.mcutil.BlockTypes;

public interface IBlockTypesAttachable
{
	IEventRegProxy block(BlockTypes block);
}
