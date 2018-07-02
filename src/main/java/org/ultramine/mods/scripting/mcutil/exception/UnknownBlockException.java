package org.ultramine.mods.scripting.mcutil.exception;

public class UnknownBlockException extends UnknownIdException
{
	public UnknownBlockException(String id)
	{
		super("Block not found for ID: " + id, id);
	}

	public UnknownBlockException(int id)
	{
		super("Block not found for ID: " + id, Integer.toString(id));
	}
}
