package org.ultramine.mods.scripting.mcutil.exception;

public class IllegalMetadataException extends RuntimeException
{
	private final int meta;

	public IllegalMetadataException(String msg, int meta)
	{
		super(msg);
		this.meta = meta;
	}

	public IllegalMetadataException(int meta)
	{
		this("Illegal metadata: " + meta, meta);
	}

	public int getMeta()
	{
		return meta;
	}
}
