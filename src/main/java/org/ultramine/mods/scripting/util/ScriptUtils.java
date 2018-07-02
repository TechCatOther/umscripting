package org.ultramine.mods.scripting.util;

import cpw.mods.fml.relauncher.ReflectionHelper;
import groovy.lang.Script;
import groovy.util.DelegatingScript;
import net.minecraft.command.CommandHandler;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import org.ultramine.commands.CommandRegistry;
import org.ultramine.commands.HandlerBasedCommand;
import org.ultramine.commands.IExtendedCommand;
import org.ultramine.mods.scripting.ScriptContainer;
import org.ultramine.mods.scripting.runtime.ScriptBaseClass;
import org.ultramine.server.util.TranslitTable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class ScriptUtils
{
	private static final Field F_usage;
	private static final Field F_description;

	private static final Field F_commandMap;
	private static final Field F_registeredCommands;

	private static final Field F_nbtTagList_list;

	static
	{
		try
		{
			F_usage = HandlerBasedCommand.class.getDeclaredField("usage");
			F_usage.setAccessible(true);

			F_description = HandlerBasedCommand.class.getDeclaredField("description");
			F_description.setAccessible(true);

			F_commandMap = CommandRegistry.class.getDeclaredField("commandMap");
			F_commandMap.setAccessible(true);

			F_registeredCommands = CommandRegistry.class.getDeclaredField("registeredCommands");
			F_registeredCommands.setAccessible(true);

			F_nbtTagList_list = ReflectionHelper.findField(NBTTagList.class, "field_74747_a", "tagList");
			F_nbtTagList_list.setAccessible(true);
		}
		catch(NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static ScriptContainer getContainer(Script script)
	{
		return ((ScriptBaseClass)script).getContainer();
	}

	public static void setCommandUsageAndDesc(HandlerBasedCommand command, String usage, String description)
	{
		try
		{
			F_usage.set(command, usage);
			F_description.set(command, description);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void unregisterCommand(HandlerBasedCommand command)
	{
		CommandRegistry reg = ((CommandHandler) MinecraftServer.getServer().getCommandManager()).getRegistry();
		try
		{
			Map<String, IExtendedCommand> commandMap = (Map<String, IExtendedCommand>)F_commandMap.get(reg);
			SortedSet<IExtendedCommand> registeredCommands = (SortedSet<IExtendedCommand>)F_registeredCommands.get(reg);

			if(registeredCommands.remove(command))
			{
				commandMap.remove(command.getCommandName());
				commandMap.remove(TranslitTable.translitENRU(command.getCommandName()));

				List<String> aliases = command.getCommandAliases();
				for(String alias : aliases)
				{
					commandMap.remove(alias);
					commandMap.remove(TranslitTable.translitENRU(alias));
				}
			}
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static List getNBTList(NBTTagList nbt)
	{
		try
		{
			return (List)F_nbtTagList_list.get(nbt);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
