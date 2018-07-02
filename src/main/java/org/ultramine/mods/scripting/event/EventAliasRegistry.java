package org.ultramine.mods.scripting.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import groovy.lang.Script;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.ultramine.mods.scripting.event.proxy.DefaultEventRegProxy;
import org.ultramine.mods.scripting.event.proxy.IEventRegProxy;
import org.ultramine.mods.scripting.event.proxy.entity.FMLPlayerEventRegProxy;
import org.ultramine.mods.scripting.event.proxy.entity.ForgeEntityEventRegProxy;
import org.ultramine.mods.scripting.event.proxy.special.BlockEventRegProxy;
import org.ultramine.mods.scripting.event.proxy.special.PlayerInteractEventRegProxy;
import org.ultramine.mods.scripting.event.proxy.global.TickEventRegProxy;
import org.ultramine.mods.scripting.event.proxy.special.SetBlockEventRegProxy;
import org.ultramine.mods.scripting.mcutil.BlockTypes;
import org.ultramine.mods.scripting.mcutil.WorldBlock;
import org.ultramine.server.event.PlayerSneakingEvent;
import org.ultramine.server.event.PlayerSwingItemEvent;
import org.ultramine.server.event.PreDimChangeEvent;

import java.util.HashMap;
import java.util.Map;

import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.LEFT_CLICK_BLOCK;
import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR;
import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK;

public class EventAliasRegistry
{
	private static Map<String, IGlobalEventRegProxyFactory> globalEvents = new HashMap<>();
	private static Map<String, ILivingEventRegProxyFactory> livingEvents = new HashMap<>();
	private static Map<String, IPlayerEventRegProxyFactory> playerEvents = new HashMap<>();
	private static Map<String, IBlockTypesEventRegProxyFactory> blocktypesEvents = new HashMap<>();
	private static Map<String, IWorldBlockEventRegProxyFactory> worldBlockEvents = new HashMap<>();

	public static void registerGlobalAlias(String alias, IGlobalEventRegProxyFactory factory)
	{
		globalEvents.put(alias, factory);
	}

	public static IEventRegProxy createGlobalProxy(Script script, String name, EventPriority prior)
	{
		return globalEvents.get(name).create(script, prior);
	}

	public static void registerLivingAlias(String alias, ILivingEventRegProxyFactory factory)
	{
		livingEvents.put(alias, factory);
	}

	public static IEventRegProxy createLivingProxy(Script script, String name, EventPriority prior, EntityLivingBase entity)
	{
		return livingEvents.get(name).create(script, prior, entity);
	}

	public static void registerPlayerAlias(String alias, IPlayerEventRegProxyFactory factory)
	{
		playerEvents.put(alias, factory);
	}

	public static IEventRegProxy createPlayerProxy(Script script, String name, EventPriority prior, EntityPlayerMP player)
	{
		return playerEvents.get(name).create(script, prior, player);
	}

	public static void registerBlockTypesAlias(String alias, IBlockTypesEventRegProxyFactory factory)
	{
		blocktypesEvents.put(alias, factory);
	}

	public static IEventRegProxy createBlockTypesProxy(Script script, String name, EventPriority prior, BlockTypes types)
	{
		return blocktypesEvents.get(name).create(script, prior, types);
	}

	public static void registerWorldBlockAlias(String alias, IWorldBlockEventRegProxyFactory factory)
	{
		worldBlockEvents.put(alias, factory);
	}

	public static IEventRegProxy createWorldBlockProxy(Script script, String name, EventPriority prior, WorldBlock block)
	{
		return worldBlockEvents.get(name).create(script, prior, block);
	}

	public static RegAliasBuilder registerAlias(String alias, IGlobalEventRegProxyFactory factory)
	{
		return new RegAliasBuilder(alias, factory);
	}

	static
	{
		registerGlobalAlias("tick", TickEventRegProxy::new);
		registerGlobalAlias("oregen_pre", (script, prior) -> new DefaultEventRegProxy(script, prior, OreGenEvent.Pre.class));
		registerGlobalAlias("oregen_post", (script, prior) -> new DefaultEventRegProxy(script, prior, OreGenEvent.Post.class));
		registerGlobalAlias("oregen_minable", (script, prior) -> new DefaultEventRegProxy(script, prior, OreGenEvent.GenerateMinable.class));

		registerAlias("click", (script, prior) -> new PlayerInteractEventRegProxy(script, prior, LEFT_CLICK_BLOCK)).player().blockTypes().worldBlock();
		registerAlias("interact", (script, prior) -> new PlayerInteractEventRegProxy(script, prior, RIGHT_CLICK_BLOCK)).player().blockTypes().worldBlock();
		registerAlias("interact_air", (script, prior) -> new PlayerInteractEventRegProxy(script, prior, RIGHT_CLICK_AIR)).player();
		registerAlias("harvest", (script, prior) -> new BlockEventRegProxy(script, BlockEvent.HarvestDropsEvent.class, prior)).player().blockTypes().worldBlock();
		registerAlias("break", (script, prior) -> new BlockEventRegProxy(script, BlockEvent.BreakEvent.class, prior)).player().blockTypes().worldBlock();
		registerAlias("place", (script, prior) -> new BlockEventRegProxy(script, BlockEvent.PlaceEvent.class, prior)).player().blockTypes().worldBlock();
		registerAlias("place_multiple", (script, prior) -> new BlockEventRegProxy(script, BlockEvent.MultiPlaceEvent.class, prior)).player().blockTypes().worldBlock();

		registerAlias("block_set", SetBlockEventRegProxy::new).worldBlock();

		registerAlias("ender_teleport", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, EnderTeleportEvent.class)).living();
		registerAlias("attacked", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, LivingAttackEvent.class)).living();
		registerAlias("hurt", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, LivingHurtEvent.class)).living();
		registerAlias("death", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, LivingDeathEvent.class)).living();
		registerAlias("drops", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, LivingDropsEvent.class)).living();
		registerAlias("fall", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, LivingFallEvent.class)).living();
		registerAlias("heal", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, LivingHealEvent.class)).living();
		registerAlias("set_attack_target", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, LivingSetAttackTargetEvent.class)).living();
		registerAlias("jump", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, LivingEvent.LivingJumpEvent.class)).living();

		registerAlias("login", (script, prior) -> new FMLPlayerEventRegProxy(script, prior, PlayerEvent.PlayerLoggedInEvent.class)).player();
		registerAlias("logout", (script, prior) -> new FMLPlayerEventRegProxy(script, prior, PlayerEvent.PlayerLoggedOutEvent.class)).player();
		registerAlias("respawn", (script, prior) -> new FMLPlayerEventRegProxy(script, prior, PlayerEvent.PlayerRespawnEvent.class)).player();
		registerAlias("dimchange", (script, prior) -> new FMLPlayerEventRegProxy(script, prior, PlayerEvent.PlayerChangedDimensionEvent.class)).player();
//		registerAlias("pickup_item", (script, prior) -> new FMLPlayerEventRegProxy(script, prior, PlayerEvent.ItemPickupEvent.class)).player(); //Forge better
		registerAlias("itemcraft", (script, prior) -> new FMLPlayerEventRegProxy(script, prior, PlayerEvent.ItemCraftedEvent.class)).player();
		registerAlias("itemsmelt", (script, prior) -> new FMLPlayerEventRegProxy(script, prior, PlayerEvent.ItemSmeltedEvent.class)).player();
		registerAlias("achievement", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, AchievementEvent.class)).player();
		registerAlias("anvil_repair", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, AnvilRepairEvent.class)).player();
		registerAlias("arrow_loose", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, ArrowLooseEvent.class)).player();
		registerAlias("arrow_nock", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, ArrowNockEvent.class)).player();
		registerAlias("attack_entity", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, AttackEntityEvent.class)).player();
		registerAlias("bonemeal", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, BonemealEvent.class)).player();
		registerAlias("interact_entity", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, EntityInteractEvent.class)).player();
		registerAlias("pickup_item", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, EntityItemPickupEvent.class)).player();
		registerAlias("fill_bucket", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, FillBucketEvent.class)).player();
		registerAlias("destroy_item", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerDestroyItemEvent.class)).player();
		registerAlias("drops", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerDropsEvent.class)).player();
		registerAlias("fall", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerFlyableFallEvent.class)).player();
		registerAlias("open_container", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerOpenContainerEvent.class)).player();
		registerAlias("pickup_xp", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerPickupXpEvent.class)).player();
		registerAlias("sleep_in_bed", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerSleepInBedEvent.class)).player();
		registerAlias("use_item", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerUseItemEvent.class)).player();
		registerAlias("wakeup", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerWakeUpEvent.class)).player();
		registerAlias("use_hoe", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, UseHoeEvent.class)).player();
		registerAlias("predimchange", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PreDimChangeEvent.class)).player();
		registerAlias("swing_item", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerSwingItemEvent.class)).player();
		registerAlias("sneaking", (script, prior) -> new ForgeEntityEventRegProxy(script, prior, PlayerSneakingEvent.class)).player();
	}

	@FunctionalInterface
	public interface IGlobalEventRegProxyFactory
	{
		IEventRegProxy create(Script script, EventPriority prior);
	}

	@FunctionalInterface
	public interface ILivingEventRegProxyFactory
	{
		IEventRegProxy create(Script script, EventPriority prior, EntityLivingBase entity);
	}

	@FunctionalInterface
	public interface IPlayerEventRegProxyFactory
	{
		IEventRegProxy create(Script script, EventPriority prior, EntityPlayerMP player);
	}

	@FunctionalInterface
	public interface IBlockTypesEventRegProxyFactory
	{
		IEventRegProxy create(Script script, EventPriority prior, BlockTypes types);
	}

	@FunctionalInterface
	public interface IWorldBlockEventRegProxyFactory
	{
		IEventRegProxy create(Script script, EventPriority prior, WorldBlock block);
	}

	public static class RegAliasBuilder
	{
		private final String name;
		private final IGlobalEventRegProxyFactory factory;

		public RegAliasBuilder(String name, IGlobalEventRegProxyFactory factory)
		{
			this.name = name;
			this.factory = factory;
			registerGlobalAlias(name, factory);
		}

		public RegAliasBuilder living()
		{
			registerLivingAlias(name, (script, prior, entity) -> ((ILivingAttachable)factory.create(script, prior)).by(entity));
			registerPlayerAlias(name, (script, prior, player) -> ((IPlayerAttachable)factory.create(script, prior)).by(player));
			return this;
		}

		public RegAliasBuilder player()
		{
			registerPlayerAlias(name, (script, prior, player) -> ((IPlayerAttachable)factory.create(script, prior)).by(player));
			return this;
		}

		public RegAliasBuilder blockTypes()
		{
			registerBlockTypesAlias(name, (script, prior, block) -> ((IBlockTypesAttachable)factory.create(script, prior)).block(block));
			return this;
		}

		public RegAliasBuilder worldBlock()
		{
			registerWorldBlockAlias(name, (script, prior, block) -> ((IWorldBlockAttachable)factory.create(script, prior)).at(block));
			return this;
		}
	}
}
