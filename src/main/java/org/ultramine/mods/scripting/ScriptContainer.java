package org.ultramine.mods.scripting;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import groovy.lang.Closure;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.commands.HandlerBasedCommand;
import org.ultramine.mods.scripting.event.EventHandlerHolder;
import org.ultramine.mods.scripting.event.IHoldedEventHandler;
import org.ultramine.mods.scripting.event.IUnregisterable;
import org.ultramine.mods.scripting.runtime.ScriptBaseClass;
import org.ultramine.mods.scripting.util.ScriptUtils;
import org.ultramine.scheduler.ScheduledTask;
import org.ultramine.server.ConfigurationHandler;
import org.ultramine.server.util.AsyncIOUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ScriptContainer
{
	private static final Logger log = LogManager.getLogger();
	private static File storageDir = new File(ConfigurationHandler.getStorageDir(), "umscripts");

	private final ScriptLoader loader;
	private final ScriptBaseClass script;
	private final File source;
	private final String scriptId;
	private final long lastUpdate;
	private final File storageFile;

	private ConfigObject storage;
	private ScriptMetadata metadata;

	private List<Closure<Void>> loadHandlers = new ArrayList<>(1);
	private List<Closure<Void>> unloadHandlers = new ArrayList<>(1);
	private List<IUnregisterable> eventHandlers = new ArrayList<>();
	private TIntObjectMap<EventHandlerHolder> entityEventHandlers = new TIntObjectHashMap<>();
	private List<HandlerBasedCommand> commands = new ArrayList<>();
	private List<ScheduledTask> scheduledTasks = new ArrayList<>();

	public ScriptContainer(ScriptLoader loader, ScriptBaseClass script, File source, String scriptId)
	{
		this.loader = loader;
		this.script = script;
		this.source = source;
		this.scriptId = scriptId;
		this.lastUpdate = source.lastModified();
		this.metadata = new ScriptMetadata(this.scriptId, "1.0", "");
		this.storageFile = new File(storageDir, this.scriptId + ".cfg");

		script.setContainer(this);
	}

	public File getSource()
	{
		return source;
	}

	public String getID()
	{
		return scriptId;
	}

	public long getLastUpdate()
	{
		return lastUpdate;
	}

	public ScriptBaseClass getScript()
	{
		return script;
	}

	public void setMetadata(ScriptMetadata metadata)
	{
		this.metadata = metadata;
	}

	public void load()
	{
		log.debug("[UMS] loading script {}", scriptId);
		long startt = System.nanoTime();
		try
		{
			if(storageFile.isFile())
				storage = new ConfigSlurper().parse(storageFile.toURI().toURL());
			else
				storage = new ConfigObject();
		}
		catch(Throwable t)
		{
			log.error("[UMS:"+ scriptId +"] Failed to load script storage file. Ignoring it", t);
		}

		try
		{
			script.run();
			for(Closure<Void> clsr : loadHandlers)
				clsr.call();
			loadHandlers.clear();
			info("script loaded ("+((System.nanoTime() - startt)/1000000)+"ms)");
		}
		catch(Throwable t)
		{
			errorOccurred("Failed to load script. Unloading", t);
		}
	}

	public void unload()
	{
		log.debug("[UMS] unloading script {}", scriptId);

		for(IUnregisterable holder : eventHandlers)
		{
			try {
				holder.doUnregister();
			} catch(Throwable t) {
				log.error("[UMS:"+ scriptId +"] Failed to inregister event handler", t);
			}
		}
		eventHandlers.clear();

		for(HandlerBasedCommand command : commands)
		{
			try {
				ScriptUtils.unregisterCommand(command);
			} catch(Throwable t) {
				log.error("[UMS:"+ scriptId +"] Failed to inregister command", t);
			}
		}
		commands.clear();

		for(ScheduledTask task : scheduledTasks)
		{
			try {
				task.cancel();
			} catch(Throwable t) {
				log.error("[UMS:"+ scriptId +"] Failed to scheduled task", t);
			}
		}
		scheduledTasks.clear();

		for(Closure<Void> clsr : unloadHandlers)
		{
			try {
				clsr.call();
			} catch(Throwable t) {
				log.error("[UMS:"+ scriptId +"] Failed to invoke unload method", t);
			}
		}

		unloadHandlers.clear();

		loader.onUnload(this);

		info("script unloaded");
	}

	public void errorOccurred(String desc, Throwable t)
	{
		log.error("[UMS:" + scriptId + "] " + desc, t);
		unload();
	}

	public void info(Object obj)
	{
		log.info("[UMS:{}] {}", scriptId, obj);
	}

	public ConfigObject getStorage()
	{
		return storage;
	}

	public void setStorage(ConfigObject storage)
	{
		this.storage = storage;
	}

	public void saveStorage()
	{
		if(this.storage != null && !this.storage.isEmpty())
		{
			StringWriter out = new StringWriter(4096);
			try
			{
				this.storage.writeTo(out);
				AsyncIOUtils.writeString(storageFile, out.toString());
			}
			catch(IOException e)
			{
				log.error("[UMS:" + scriptId + "] Failed to write storage file: " + storageFile, e);
			}
		}
		else
		{
			if(storageFile.exists())
				storageFile.delete();
		}
	}

	public void addLoadHandler(Closure<Void> clsr)
	{
		loadHandlers.add(clsr);
	}

	public void addUnloadHandler(Closure<Void> clsr)
	{
		unloadHandlers.add(clsr);
	}

	public void registerEvent(Class<? extends Event> eventType, EventPriority priority, IHoldedEventHandler handler)
	{
		EventHandlerHolder holder = new EventHandlerHolder(this, eventType, priority, handler);
		holder.register();
		eventHandlers.add(holder);
	}

	public void registerEvent(Class<? extends Event> eventType, EventPriority priority, Entity attachedToEntity, IHoldedEventHandler handler)
	{
		EventHandlerHolder holder = new EventHandlerHolder(this, eventType, priority, handler);
		holder.register();
		holder.attachToEntity(attachedToEntity);
		entityEventHandlers.put(attachedToEntity.getEntityId(), holder);
		eventHandlers.add(holder);
	}

	public void unregisterHandler(EventHandlerHolder handler)
	{
		eventHandlers.remove(handler);
		if(handler.getAttachedToEntity() != null)
			entityEventHandlers.remove(handler.getAttachedToEntity().getEntityId());
		handler.doUnregister();
	}

	public void unregisterAllHandlers()
	{
		for(IUnregisterable holder : eventHandlers)
			holder.doUnregister();
		eventHandlers.clear();
	}

	public void registerCommand(HandlerBasedCommand command)
	{
		((CommandHandler)MinecraftServer.getServer().getCommandManager()).getRegistry().registerCommand(command);
		commands.add(command);
	}

	public void onPlayerLogout(EntityPlayerMP player)
	{
		EventHandlerHolder holder = entityEventHandlers.remove(player.getEntityId());
		if(holder != null)
			unregisterHandler(holder);
	}

	public void scheduleSync(String pattern, Closure<Void> clsr)
	{
		ScheduledTask task = MinecraftServer.getServer().getScheduler().scheduleSync(pattern, () -> {
			try
			{
				clsr.call();
			}
			catch(Throwable t)
			{
				errorOccurred("Failed to execute scheduled task", t);
			}
		});

		scheduledTasks.add(task);
	}
}
