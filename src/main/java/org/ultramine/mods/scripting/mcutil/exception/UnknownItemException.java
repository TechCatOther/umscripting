package org.ultramine.mods.scripting.mcutil.exception;

public class UnknownItemException extends UnknownIdException
{
	public UnknownItemException(String id)
	{
		super("Item not found for ID: " + id, id);
	}

	public UnknownItemException(int id)
	{
		super("Item not found for ID: " + id, Integer.toString(id));
	}
}
