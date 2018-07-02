package org.ultramine.mods.scripting;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.ultramine.mods.scripting.mcutil.internal.TempPlayerData;

import java.io.File;

@Mod(modid = "UMScripting", name = "UltraMine Scripting API", version = "@version@", acceptableRemoteVersions = "*")
public class UMScripting
{
	@Mod.Instance("UMScripting")
	private static UMScripting instance;

	private final ScriptLoader loader = new ScriptLoader(new File("umscripts"));

	public static UMScripting instance()
	{
		return instance;
	}

	public ScriptLoader getLoader()
	{
		return loader;
	}

	@Mod.EventHandler
	public void serverStartind(FMLServerStartingEvent e)
	{
		e.registerCommands(ControlCommands.class);
		e.getServer().getConfigurationManager().getDataLoader().registerPlayerDataExt(TempPlayerData.class, "ums");

		GlobalEventHandler eh = new GlobalEventHandler();
		FMLCommonHandler.instance().bus().register(eh);
		MinecraftForge.EVENT_BUS.register(eh);
	}

	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent e)
	{
		loader.bootstrap();
	}
}
