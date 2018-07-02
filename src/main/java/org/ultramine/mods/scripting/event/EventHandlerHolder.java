package org.ultramine.mods.scripting.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.IEventListener;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import org.ultramine.mods.scripting.ScriptContainer;
import org.ultramine.mods.scripting.UMScripting;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventHandlerHolder implements IEventListener, IUnregisterable
{
	private static final Field F_listeners;
	private static final Field F_listenerOwners;
	private static final Field F_busID;

	static
	{
		try
		{
			F_listeners = EventBus.class.getDeclaredField("listeners");
			F_listeners.setAccessible(true);

			F_listenerOwners = EventBus.class.getDeclaredField("listenerOwners");
			F_listenerOwners.setAccessible(true);

			F_busID = EventBus.class.getDeclaredField("busID");
			F_busID.setAccessible(true);
		}
		catch(NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}

	private final ScriptContainer script;
	private final Class<? extends Event> eventType;
	private final EventPriority priority;
	private final IHoldedEventHandler handler;
	private final List<EventBus> registeredOn = new ArrayList<EventBus>(1);

	private boolean unregistered = false;
	private Entity attachedToEntity;

	public EventHandlerHolder(ScriptContainer script, Class<? extends Event> eventType, EventPriority priority, IHoldedEventHandler handler)
	{
		if(!Event.class.isAssignableFrom(eventType))
			throw new IllegalArgumentException("eventType("+eventType.getName()+") is not assignable from Event class");
		this.script = script;
		this.eventType = eventType;
		this.priority = priority;
		this.handler = handler;


		handler.setHolder(this);
	}

	public void attachToEntity(Entity attachedToEntity)
	{
		if(this.attachedToEntity != null)
			throw new IllegalStateException("Entity already attached");
		this.attachedToEntity = attachedToEntity;
	}

	public Entity getAttachedToEntity()
	{
		return attachedToEntity;
	}

	public void register()
	{
		if(eventType.getName().startsWith("cpw.mods.fml.common."))
			register(FMLCommonHandler.instance().bus());
		else if(OreGenEvent.class.isAssignableFrom(eventType))
			register(MinecraftForge.ORE_GEN_BUS);
		else if(eventType.getName().startsWith("net.minecraftforge.event.terraingen."))
			register(MinecraftForge.TERRAIN_GEN_BUS);
		else
			register(MinecraftForge.EVENT_BUS);
	}

	public void register(EventBus bus)
	{
		if(unregistered)
			throw new IllegalStateException("unregistered");
		bus.getClass(); //NPE
		try
		{
			Map<Object, ArrayList<IEventListener>> listeners = getListeners(bus);
			if(listeners.containsKey(this))
				return;
			ArrayList<IEventListener> llist = new ArrayList<IEventListener>(1);
			llist.add(this);
			listeners.put(this, llist);
			getListenerOwners(bus).put(this, Loader.instance().getReversedModObjectList().get(UMScripting.instance()));

			Constructor<?> ctr = eventType.getConstructor();
			ctr.setAccessible(true);
			Event event = (Event)ctr.newInstance();
			event.getListenerList().register(getBusID(bus), priority, this);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		registeredOn.add(bus);
	}

	public void doUnregister()
	{
		for(EventBus bus : registeredOn)
			bus.unregister(this);
		registeredOn.clear();
		unregistered = true;
	}

	public void unregister()
	{
		script.unregisterHandler(this);
	}

	@Override
	public void invoke(Event event)
	{
		if(unregistered)
			return;

		if(attachedToEntity != null && attachedToEntity.isDead)
		{
			unregister();
			return;
		}

		if(event.isCancelable() && event.isCanceled())
			return;

		try
		{
			handler.invoke(event);
		}
		catch(Throwable t)
		{
			script.errorOccurred("Failed to handle event " + event.getClass().getName(), t);
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<Object, ArrayList<IEventListener>> getListeners(EventBus bus) throws IllegalAccessException
	{
		return (Map<Object, ArrayList<IEventListener>>)F_listeners.get(bus);
	}

	@SuppressWarnings("unchecked")
	private static Map<Object, ModContainer> getListenerOwners(EventBus bus) throws IllegalAccessException
	{
		return (Map<Object, ModContainer>)F_listenerOwners.get(bus);
	}

	private static int getBusID(EventBus bus) throws IllegalAccessException
	{
		return F_busID.getInt(bus);
	}
}
