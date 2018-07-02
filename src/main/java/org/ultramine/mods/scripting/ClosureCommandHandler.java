package org.ultramine.mods.scripting;

import groovy.lang.Closure;
import org.ultramine.commands.CommandContext;
import org.ultramine.commands.ICommandHandler;

public class ClosureCommandHandler implements ICommandHandler
{
	private final Closure<Void> handler;

	public ClosureCommandHandler(Closure<Void> handler)
	{
		this.handler = handler;
//		handler.setResolveStrategy(Closure.DELEGATE_FIRST);
	}

	@Override
	public void processCommand(CommandContext context)
	{
		handler.setDelegate(context);
		try
		{
			handler.call();
		}
		finally
		{
			handler.setDelegate(null);
		}
	}
}
