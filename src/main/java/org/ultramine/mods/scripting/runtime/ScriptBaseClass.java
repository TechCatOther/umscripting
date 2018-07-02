package org.ultramine.mods.scripting.runtime;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import groovy.lang.Closure;
import groovy.lang.Script;
import groovy.util.ConfigObject;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import org.ultramine.commands.HandlerBasedCommand;
import org.ultramine.commands.syntax.ArgumentsPatternParser;
import org.ultramine.core.permissions.MinecraftPermissions;
import org.ultramine.mods.scripting.ClosureCommandHandler;
import org.ultramine.mods.scripting.ScriptContainer;
import org.ultramine.mods.scripting.ScriptLoader;
import org.ultramine.mods.scripting.ScriptMetadata;
import org.ultramine.mods.scripting.util.ScriptUtils;
import org.ultramine.mods.scripting.event.EventAliasRegistry;
import org.ultramine.mods.scripting.event.IAliasedEventRegistry;
import org.ultramine.mods.scripting.event.proxy.global.GlobalRegistrationProxy;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;
import org.ultramine.mods.scripting.event.IGlobalEventRegistry;
import org.ultramine.mods.scripting.event.IEventSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class ScriptBaseClass extends Script implements IEventSource, IGlobalEventRegistry, IAliasedEventRegistry
{
	private ScriptContainer container;
	private final MinecraftServer server = MinecraftServer.getServer();

	public void setContainer(ScriptContainer container)
	{
		this.container = container;
	}

	public ScriptContainer getContainer()
	{
		return container;
	}

	@Override
	public void println()
	{

	}

	@Override
	public void println(Object value)
	{
		container.info(value);
	}

	@Override
	public void print(Object value)
	{
		container.info(value);
	}

	@Override
	public void printf(String format, Object value)
	{
		container.info(String.format(format, value));
	}

	@Override
	public void printf(String format, Object[] values)
	{
		container.info(String.format(format, values));
	}

	public void println(IChatComponent value)
	{
		container.info(value.getFormattedText());
	}

	public void print(IChatComponent value)
	{
		container.info(value.getFormattedText());
	}

	public void errorOccurred(String desc, Throwable t)
	{
		container.errorOccurred(desc, t);
	}

	public void errorOccurred(Throwable t)
	{
		container.errorOccurred("unknown error", t);
	}

	public void load(Closure<Void> clsr)
	{
		clsr.setResolveStrategy(Closure.DELEGATE_FIRST);
		container.addLoadHandler(clsr);
	}

	public void unload(Closure<Void> clsr)
	{
		clsr.setResolveStrategy(Closure.DELEGATE_FIRST);
		container.addUnloadHandler(clsr);
	}

	public void meta(Closure<Void> clsr)
	{
		clsr.setResolveStrategy(Closure.DELEGATE_FIRST);
		ScriptMetaBuilder meta = new ScriptMetaBuilder();
		clsr.setDelegate(meta);
		clsr.call();
		container.setMetadata(meta.build());
	}

	public void command(Closure<Void> clsr)
	{
		clsr.setResolveStrategy(Closure.DELEGATE_FIRST);
		CommandBuilder data = new CommandBuilder();
		clsr.setDelegate(data);
		clsr.call();

		if(data.name == null || data.handler == null)
			throw new IllegalArgumentException("Command name or handler has not set");
		if(data.group == null)
			data.group = container.getID();
		if(data.permissions.isEmpty())
			data.permissions.add(MinecraftPermissions.OP);
		if(data.syntax.isEmpty())
			data.syntax.add("");
		if(data.usage == null)
			data.usage = "/"+data.name;
		if(data.description == null)
			data.description = "";

		HandlerBasedCommand.Builder builder = new HandlerBasedCommand.Builder(data.name, data.group, new ClosureCommandHandler(data.handler))
				.setAliases(data.aliases.toArray(new String[data.aliases.size()]))
				.setPermissions(data.permissions.toArray(new String[data.permissions.size()]))
				.setUsableFromServer(data.fromConsole);

		ArgumentsPatternParser parser = ((CommandHandler)server.getCommandManager()).getRegistry().getArgumentsParser();
		for (String completion : data.syntax)
			builder.addArgumentsPattern(parser.parse(completion));

		HandlerBasedCommand command = builder.build();
		ScriptUtils.setCommandUsageAndDesc(command, data.usage, data.description);
		container.registerCommand(command);
	}

	public void async(Closure<Void> clsr)
	{
		AsyncTaskDelegate delegate = new AsyncTaskDelegate();
		clsr.setResolveStrategy(Closure.DELEGATE_FIRST);
		clsr.setDelegate(delegate);
		ScriptLoader.executor.execute(v -> clsr.call(), v -> {
			if(delegate.readyCallback != null)
			{
				try
				{
					delegate.readyCallback.call();
				} catch(Throwable t)
				{
					errorOccurred("Failed to run async callback", t);
				}
			}
			return null;
		});
	}

	@Override
	public IAliasedEventRegistry getOn()
	{
		return this;
	}

	@Override
	public IEventRegProxy getAt(Class<? extends Event> cls, EventPriority prior)
	{
		return new GlobalRegistrationProxy(container, cls, prior);
	}

	@Override
	public IEventRegProxy getAt(Script script, String type, EventPriority prior)
	{
		return EventAliasRegistry.createGlobalProxy(this, type, prior);
	}

	public void unregisterAll()
	{
		container.unregisterAllHandlers();
	}

	public ConfigObject getStorage()
	{
		return container.getStorage();
	}

	public void setStorage(ConfigObject storage)
	{
		container.setStorage(storage);
	}

	public void setStorage(Map<?, ?> map)
	{
		ConfigObject storage = new ConfigObject();
		storage.putAll(map);
		container.setStorage(storage);
	}

	public void saveStorage()
	{
		container.saveStorage();
	}

	public void schedule(String pattern, Closure<Void> task)
	{
		container.scheduleSync(pattern, task);
	}

	public class ScriptMetaBuilder
	{
		public String name = container.getID();
		public String version = "1.0";
		public String author = "";

		public void name(String name)
		{
			this.name = name;
		}

		public void version(String version)
		{
			this.version = version;
		}

		public void author(String author)
		{
			this.author = author;
		}

		public ScriptMetadata build()
		{
			return new ScriptMetadata(name, version, author);
		}
	}

	public class CommandBuilder
	{
		public String name;
		public String group = container.getID();
		public String usage;
		public String description;
		public List<String> syntax = new ArrayList<>();
		public List<String> aliases = new ArrayList<>();
		public List<String> permissions = new ArrayList<>();
		public boolean fromConsole = true;
		public Closure<Void> handler;


		public void name(String name)
		{
			this.name = name;
		}

		public void group(String group)
		{
			this.group = group;
		}

		public void usage(String usage)
		{
			this.usage = usage;
		}

		public void description(String description)
		{
			this.description = description;
		}

		public void syntax(String... syntax)
		{
			this.syntax.addAll(Arrays.asList(syntax));
		}

		public void aliases(String... aliases)
		{
			this.aliases.addAll(Arrays.asList(aliases));
		}

		public void permissions(String... permissions)
		{
			this.permissions.addAll(Arrays.asList(permissions));
		}

		public void fromConsole(boolean fromConsole)
		{
			this.fromConsole = fromConsole;
		}

		public void handler(Closure<Void> handler)
		{
			this.handler = handler;
		}
	}

	public class AsyncTaskDelegate
	{
		private Closure<Void> readyCallback;

		public void ready(Closure<Void> readyCallback)
		{
			this.readyCallback = readyCallback;
		}
	}
}
