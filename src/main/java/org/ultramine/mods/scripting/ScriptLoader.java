package org.ultramine.mods.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.ultramine.mods.scripting.runtime.ScriptBaseClass;
import org.ultramine.server.util.GlobalExecutors;
import org.ultramine.server.util.TwoStepsExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScriptLoader
{
	private static final Logger log = LogManager.getLogger();
	public static final TwoStepsExecutor executor = new TwoStepsExecutor(GlobalExecutors.cachedExecutor());

	private final File scriptDir;
	private CompilerConfiguration cfg;

	private Map<String, ScriptContainer> loadedScripts = new HashMap<>();
	private Map<File, ScriptContainer> sourceToScript = new HashMap<>();

	private boolean firstLoaded = false;

	public ScriptLoader(File scriptDir)
	{
		this.scriptDir = scriptDir;
		scriptDir.mkdirs();
		boolean obfenv = false;
		try
		{
			MinecraftServer.class.getDeclaredMethod("getServer");
		}
		catch(NoSuchMethodException e)
		{
			obfenv = true;
		}
		GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(new CustomMetaClassCreationHandle(obfenv));
		prepareEnv();
		executor.register();
	}

	private void prepareEnv()
	{
		cfg = new CompilerConfiguration();
		ImportCustomizer imp = new ImportCustomizer();
		imp.addStarImports(
				"cpw.mods.fml.common.event",
				"cpw.mods.fml.common.eventhandler",
				"cpw.mods.fml.common.gameevent",
				"cpw.mods.fml.common.registry",

				"net.minecraft.potion",

				"net.minecraftforge.event",
				"net.minecraftforge.event.brewing",
				"net.minecraftforge.event.entity",
				"net.minecraftforge.event.entity.item",
				"net.minecraftforge.event.entity.living",
				"net.minecraftforge.event.entity.minecart",
				"net.minecraftforge.event.entity.player",
				"net.minecraftforge.event.entity.world",

				"org.ultramine.server.event",

				"org.ultramine.mods.scripting.mcutil"
		);
		imp.addImports(
				"groovy.transform.Canonical",
				"groovy.sql.Sql",

				"net.minecraft.block.Block",
				"net.minecraft.entity.Entity",
				"net.minecraft.entity.player.EntityPlayerMP",
				"net.minecraft.init.Blocks",
				"net.minecraft.init.Items",
				"net.minecraft.item.Item",
				"net.minecraft.item.ItemStack",
				"net.minecraft.inventory.IInventory",
				"net.minecraft.nbt.NBTTagCompound",
				"net.minecraft.server.MinecraftServer",
				"net.minecraft.tileentity.TileEntity",
				"net.minecraft.world.WorldServer",

//				"org.ultramine.economy.CurrencyRegistry",
//				"org.ultramine.economy.Currency",
//				"org.ultramine.economy.IHoldings",
//				"org.ultramine.economy.Account",
//				"org.ultramine.economy.Accounts",
				"org.ultramine.server.data.player.PlayerData",
				"org.ultramine.server.world.WorldDescriptor"
		);
		imp.addImport("NBT", "net.minecraft.nbt.NBTTagCompound");
		imp.addStaticStars(
				"net.minecraft.util.EnumChatFormatting",
				"net.minecraft.world.WorldSettings.GameType",
				"cpw.mods.fml.common.eventhandler.EventPriority",
				"net.minecraftforge.common.util.ForgeDirection",

				"org.ultramine.mods.scripting.runtime.StaticUtils"
		);
		imp.addStaticImport("_", "net.minecraftforge.oredict.OreDictionary", "WILDCARD_VALUE");
		cfg.addCompilationCustomizers(imp);
		cfg.setScriptBaseClass(ScriptBaseClass.class.getName());
		cfg.setSourceEncoding("UTF-8");
	}

	public void bootstrap()
	{
		internalLoadAll();
	}

	private void internalLoadAll()
	{
		internalDir(scriptDir);
	}

	private void internalDir(File dir)
	{
		for(File file : dir.listFiles())
		{
			if(file.isFile() && !sourceToScript.containsKey(file))
				loadScript(file);
			else if(file.isDirectory())
				internalDir(file);
		}
	}

	private void loadScript(final File file)
	{
		if(!file.isFile() || !file.getName().endsWith(".groovy"))
			return;
		if(!firstLoaded) //groovy bug? can't bootstrap multithreaded
		{
			applyScript(file, compileScript(file));
			firstLoaded = true;
			return;
		}
		executor.execute(v -> compileScript(file), script -> {applyScript(file, script); return null;});
	}

	private ScriptBaseClass compileScript(final File file)
	{
		GroovyShell eng = new GroovyShell(ScriptLoader.class.getClassLoader(), new Binding(), cfg);
		ScriptBaseClass script = null;
		try
		{
			script = (ScriptBaseClass)eng.parse(FileUtils.readFileToString(file, Charsets.UTF_8), getScriptId(file, "__"));
		}
		catch(MultipleCompilationErrorsException e)
		{
			log.error("Failed to compile script " + file.getName(), e);
			for(int i = 0; i < e.getErrorCollector().getErrorCount(); i++)
			{
				Exception ex = e.getErrorCollector().getException(i);
				if(ex != null)
					log.error("Cause: ", ex);
			}
			return null;
		}
		catch(Exception e)
		{
			log.error("Failed to compile script " + file.getName(), e);
		}
		return script;
	}

	private void applyScript(File file, ScriptBaseClass script)
	{
		if(script == null)
			return;

		ScriptContainer sc = new ScriptContainer(this, script, file, getScriptId(file, "."));
		if(loadedScripts.containsKey(sc.getID()))
			unload(sc.getID());
		sc.load();
		loadedScripts.put(sc.getID(), sc);
		sourceToScript.put(file, sc);
	}

	private String getScriptId(File file, String sep)
	{
		return file.getPath().substring(scriptDir.getPath().length()+1).replace(File.separator, sep).replace(".groovy", "");
	}

	void onUnload(ScriptContainer sc)
	{
		loadedScripts.remove(sc.getID());
		sourceToScript.remove(sc.getSource());
	}

	public void unloadAll()
	{
		for(ScriptContainer sc : new ArrayList<>(loadedScripts.values()))
			sc.unload();
	}

	public void loadAll()
	{
		for(ScriptContainer sc : new ArrayList<>(loadedScripts.values()))
			if(!sc.getSource().exists() || sc.getSource().lastModified() > sc.getLastUpdate())
				sc.unload();

		internalLoadAll();
	}

	public void reloadAll()
	{
		unloadAll();
		internalLoadAll();
	}

	public boolean unload(String script)
	{
		ScriptContainer sc = loadedScripts.get(script);
		if(sc != null)
			sc.unload();
		return sc != null;
	}

	public boolean load(String script)
	{
		ScriptContainer sc = loadedScripts.get(script);
		if(sc != null)
		{
			if(!sc.getSource().exists() || sc.getSource().lastModified() > sc.getLastUpdate())
			{
				sc.unload();
				if(sc.getSource().exists())
					loadScript(sc.getSource());
				else
					return false;
			}

			return true;
		}
		else
		{
			File file = new File(scriptDir, script);
			if(file.isFile())
			{
				loadScript(file);
				return true;
			}

			return false;
		}
	}

	public boolean reload(String script)
	{
		ScriptContainer sc = loadedScripts.get(script);
		if(sc != null)
		{
			sc.unload();
			if(sc.getSource().exists())
			{
				loadScript(sc.getSource());
				return true;
			}
		}

		return false;
	}

	public Collection<ScriptContainer> getAllContainers()
	{
		return loadedScripts.values();
	}
}
