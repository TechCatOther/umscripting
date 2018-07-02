package org.ultramine.mods.scripting.deobf;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.objectweb.asm.Type;
import org.ultramine.server.util.Resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DeobfManager
{
	private static final DeobfManager instance = new DeobfManager();

	static
	{
		instance.loadMapping();
	}

	public static DeobfManager getInstance()
	{
		return instance;
	}

	private Map<String, Map<String, String>> rawFieldMaps = new HashMap<>();
	private Map<String, Map<String, String>> rawMethodMaps = new HashMap<>();

	private void loadMapping()
	{
		Splitter splitter = Splitter.on(CharMatcher.anyOf(": ")).omitEmptyStrings().trimResults();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(Resources.getAsStream("/mcp2srg.srg")));)
		{
			String line;
			while((line = reader.readLine()) != null)
			{
				String[] parts = Iterables.toArray(splitter.split(line), String.class);
				String typ = parts[0];
				if("MD".equals(typ))
				{
					parseMethod(parts);
				}
				else if("FD".equals(typ))
				{
					parseField(parts);
				}
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void parseMethod(String[] parts)
	{
		String oldSrg = parts[1];
		int lastOld = oldSrg.lastIndexOf('/');
		String cl = oldSrg.substring(0,lastOld);
		String oldName = oldSrg.substring(lastOld+1);
//		String sig = parts[2];
		String newSrg = parts[3];
		int lastNew = newSrg.lastIndexOf('/');
		String newName = newSrg.substring(lastNew+1);
		Map<String, String> map = rawMethodMaps.get(cl);
		if(map == null)
		{
			map = new HashMap<>();
			rawMethodMaps.put(cl, map);
		}
		map.put(oldName, newName);
	}

	private void parseField(String[] parts)
	{
		String oldSrg = parts[1];
		int lastOld = oldSrg.lastIndexOf('/');
		String cl = oldSrg.substring(0,lastOld);
		String oldName = oldSrg.substring(lastOld+1);
		String newSrg = parts[2];
		int lastNew = newSrg.lastIndexOf('/');
		String newName = newSrg.substring(lastNew+1);
		Map<String, String> map = rawFieldMaps.get(cl);
		if(map == null)
		{
			map = new HashMap<>();
			rawFieldMaps.put(cl, map);
		}
		map.put(oldName, newName);
	}

	public String mapMethodName(Class<?> cls, String name)
	{
		if(cls == Object.class || cls == null)
			return null;
		Map<String, String> map = rawMethodMaps.get(Type.getInternalName(cls));
		String ret = map == null ? null : map.get(name);
		if(ret != null)
			return ret;
		ret = mapMethodName(cls.getSuperclass(), name);
		if(ret != null)
			return ret;
		for(Class<?> iface : cls.getInterfaces())
		{
			ret = mapMethodName(iface, name);
			if(ret != null)
				return ret;
		}
		return null;
	}

	public String mapFieldName(Class<?> cls, String name)
	{
		String ret = null;
		while(ret == null && cls != Object.class)
		{
			Map<String, String> map = rawFieldMaps.get(Type.getInternalName(cls));
			ret = map == null ? null : map.get(name);
			cls = cls.getSuperclass();
		}
		return ret;
	}
}
