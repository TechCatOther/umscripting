package org.ultramine.mods.scripting.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class CorePlugin implements IFMLLoadingPlugin
{
	private static Logger log = LogManager.getLogger();

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		//http://central.maven.org/maven2/org/codehaus/groovy/groovy-all/2.4.3/groovy-all-2.4.3.jar
		File groovyLib = new File((File)data.get("mcLocation"), "mods/scripting/groovy-all.jar");
		if(!groovyLib.exists())
		{
			log.info("Downloading groovy runtime library");
			groovyLib.getParentFile().mkdirs();
			try
			{
				URL url = new URL("http://central.maven.org/maven2/org/codehaus/groovy/groovy-all/2.4.4/groovy-all-2.4.4.jar");
				try (
						InputStream is = url.openConnection().getInputStream();
						FileOutputStream out = new FileOutputStream(groovyLib);
					)
				{
					IOUtils.copyLarge(is, out);
				}
			}
			catch (Exception e)
			{
				log.fatal("Failed to download groovy runtime library! Abortin server loading", e);
				throw new RuntimeException("Failed to download groovy runtime library", e);
			}
		}


		LaunchClassLoader cl = (LaunchClassLoader)this.getClass().getClassLoader();
		try
		{
			cl.addURL(groovyLib.toURI().toURL());
		} catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
		cl.addTransformerExclusion("org.ultramine.mods.scripting.");
		cl.addTransformerExclusion("groovy.");
		cl.addTransformerExclusion("groovyjarjarantlr.");
		cl.addTransformerExclusion("groovyjarjarasm.");
		cl.addTransformerExclusion("groovyjarjarcommonscli.");
		cl.addTransformerExclusion("org.codehaus.groovy.");
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
