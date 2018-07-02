package org.ultramine.mods.scripting.event.proxy.entity;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import groovy.lang.Script;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent;
import org.ultramine.mods.scripting.ScriptContainer;
import org.ultramine.mods.scripting.util.ScriptUtils;
import org.ultramine.mods.scripting.event.ClosureEventHandler;
import org.ultramine.mods.scripting.event.ILivingAttachable;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;

public class ForgeEntityEventRegProxy implements IEventRegProxy, ILivingAttachable
{
	private final Script script;
	private final EventPriority prior;
	private final Class<? extends EntityEvent> eventType;
	private Entity entity;

	public ForgeEntityEventRegProxy(Script script, EventPriority prior, Class<? extends EntityEvent> eventType)
	{
		this.script = script;
		this.prior = prior;
		this.eventType = eventType;
	}

	@Override
	public void call(Closure<Void> clsr)
	{
		ScriptContainer cont = ScriptUtils.getContainer(script != null ? script : (Script) clsr.getThisObject());
		if(entity == null)
			cont.registerEvent(eventType, prior, new ClosureEventHandler(clsr));
		else
			cont.registerEvent(eventType, prior, entity, new EntitySpecificClosureEventHandler(clsr, entity.getEntityId()));
	}

	public ForgeEntityEventRegProxy by(Entity entity)
	{
		this.entity = entity;
		return this;
	}

	@Override
	public ForgeEntityEventRegProxy by(EntityLivingBase living)
	{
		return by((Entity)living);
	}

	public void by(Entity player, Closure<Void> clsr)
	{
		by(player).call(clsr);
	}

	private static class EntitySpecificClosureEventHandler extends ClosureEventHandler
	{
		private final int entityID;

		public EntitySpecificClosureEventHandler(Closure<Void> handler, int entityID)
		{
			super(handler);
			this.entityID = entityID;
		}

		@Override
		public void invoke(Event event)
		{
			if(((EntityEvent)event).entity.getEntityId() == entityID)
				super.invoke(event);
		}
	}
}
