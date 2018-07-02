package org.ultramine.mods.scripting;

import org.ultramine.commands.Command;
import org.ultramine.commands.CommandContext;

public class ControlCommands
{
	@Command(
			name = "umscript",
			group = "umscripting",
			aliases = {"ums"},
			permissions = {"command.umscript", "command.umscripting.umscript", "umscripting.command", "umscripting.command.umscript"},
			syntax = {
					"[load unload reload]",
					"[load unload reload] <%script>"
			}
	)
	public static void umscript(CommandContext ctx)
	{
		ScriptLoader loader = UMScripting.instance().getLoader();
		String script = ctx.contains("script") ? ctx.get("script").asString() : null;
		switch(ctx.getAction())
		{
			case "load":
				if(script != null)
					loader.load(script);
				else
					loader.loadAll();
				break;
			case "unload":
				if(script != null)
					loader.unload(script);
				else
					loader.unloadAll();
				break;
			case "reload":
				if(script != null)
					loader.reload(script);
				else
					loader.reloadAll();
				break;
		}

		ctx.sendMessage(ctx.getAction()+"ed");
	}
}
