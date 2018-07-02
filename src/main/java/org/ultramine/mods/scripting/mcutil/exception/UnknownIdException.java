package org.ultramine.mods.scripting.mcutil.exception;

public class UnknownIdException extends RuntimeException
{
	private final String id;

	public UnknownIdException(String msg, String id)
	{
		super(msg);
		this.id = id;
	}

	public UnknownIdException(String msg, int id)
	{
		super(msg);
		this.id = Integer.toString(id);
	}

	public String getID()
	{
		return id;
	}
}
