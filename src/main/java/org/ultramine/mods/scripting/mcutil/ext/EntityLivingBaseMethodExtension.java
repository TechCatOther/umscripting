package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.entity.EntityLivingBase;
import org.ultramine.mods.scripting.event.EventAliasRegistry;
import org.ultramine.mods.scripting.event.IAliasedEventRegistry;

public class EntityLivingBaseMethodExtension
{
	public static IAliasedEventRegistry getOn(EntityLivingBase self)
	{
		return (script, type, prior) -> EventAliasRegistry.createLivingProxy(script, type, prior, self);
	}
}
