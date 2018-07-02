package org.ultramine.mods.scripting.runtime;

import com.mojang.authlib.GameProfile;
import groovy.sql.Sql;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.ultramine.mods.scripting.mcutil.BlockState;
import org.ultramine.mods.scripting.mcutil.BlockTypes;
import org.ultramine.mods.scripting.mcutil.GameUtil;
import org.ultramine.mods.scripting.mcutil.exception.UnknownWorldException;
import org.ultramine.server.data.Databases;
import org.ultramine.server.data.player.PlayerData;
import org.ultramine.server.util.BasicTypeParser;
import org.ultramine.server.world.WorldDescriptor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class StaticUtils
{
	private static final MinecraftServer server = MinecraftServer.getServer();

	public static MinecraftServer getServer()
	{
		return server;
	}

	public static Block blockType(String id)
	{
		return GameUtil.getBlock(id);
	}

	public static Block blockType(int id)
	{
		return GameUtil.getBlock(id);
	}

	public static Block blockType(Item item)
	{
		return GameUtil.getBlock(item);
	}

	public static BlockTypes blockTypes(Object... objs)
	{
		return BlockTypes.of(objs);
	}

	public static Item itemType(String id)
	{
		return GameUtil.getItem(id);
	}

	public static Item itemType(int id)
	{
		return GameUtil.getItem(id);
	}

	public static ItemStack itemStack(String id)
	{
		return BasicTypeParser.parseItemStack(id, false, 1, true);
	}

	public static ItemStack itemStack(Item item)
	{
		return new ItemStack(item);
	}

	public static ItemStack itemStack(int id)
	{
		return new ItemStack(itemType(id));
	}

	public static ItemStack itemStack(String id, int meta)
	{
		return new ItemStack(itemType(id), 1, meta);
	}

	public static ItemStack itemStack(Item item, int meta)
	{
		return new ItemStack(item, 1, meta);
	}

	public static ItemStack itemStack(int id, int meta)
	{
		return new ItemStack(itemType(id), 1, meta);
	}

	public static ItemStack itemStack(BlockState state)
	{
		return new ItemStack(Item.getItemFromBlock(state.getType()), 1, state.getMeta());
	}

	public static BlockState blockState(Block block, int meta)
	{
		return new BlockState(block, meta);
	}

	public static BlockState blockState(String block, int meta)
	{
		return new BlockState(blockType(block), meta);
	}

	public static BlockState blockState(int block, int meta)
	{
		return new BlockState(blockType(block), meta);
	}

	public static BlockState blockState(Item item, int meta)
	{
		return new BlockState(blockType(item), meta);
	}

	public static BlockState blockState(Block block)
	{
		return new BlockState(block, 0);
	}

	public static BlockState blockState(String block)
	{
		return new BlockState(blockType(block), 0);
	}

	public static BlockState blockState(int block)
	{
		return new BlockState(blockType(block), 0);
	}

	public static BlockState blockState(Item item)
	{
		return new BlockState(blockType(item), 0);
	}

	@Nullable public static EntityPlayerMP getPlayer(String username)
	{
		return server.getConfigurationManager().getPlayerByUsername(username);
	}

	@Nullable public static EntityPlayerMP getPlayer(GameProfile profile)
	{
		return getPlayer(profile.getName());
	}

	@Nullable public static PlayerData getPlayerData(String username)
	{
		return server.getConfigurationManager().getDataLoader().getPlayerData(username);
	}

	@Nullable public static PlayerData getPlayerData(GameProfile profile)
	{
		return server.getConfigurationManager().getDataLoader().getPlayerData(profile);
	}

	@SuppressWarnings("unchecked")
	public static List<EntityPlayerMP> getPlayers()
	{
		return (List<EntityPlayerMP>)server.getConfigurationManager().playerEntityList;
	}

	public static WorldDescriptor getWorld(int dim)
	{
		WorldDescriptor desc = server.getMultiWorld().getDescByID(dim);
		if(desc == null)
			throw new UnknownWorldException("World not found for dimension: " + dim);
		return desc;
	}

	public static WorldDescriptor getWorld(String name)
	{
		WorldDescriptor desc = server.getMultiWorld().getDescByName(name);
		if(desc == null)
			throw new UnknownWorldException("World not found for name: " + name);
		return desc;
	}

	public static void broadcast(IChatComponent msg)
	{
		getServer().getConfigurationManager().sendChatMsg(msg);
	}

	public static void broadcast(String msg)
	{
		getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(msg));
	}

	public static Sql getSql(String name)
	{
		return new Sql(Databases.getDataSource(name));
	}

	public static int randomInt(int bound)
	{
		return ThreadLocalRandom.current().nextInt(bound);
	}

	public static int randomInt(int origin, int bound)
	{
		return ThreadLocalRandom.current().nextInt(origin, bound);
	}

	public boolean randomChance(float chance)
	{
		if(chance < 0f || chance > 1.0f)
			throw new IllegalArgumentException("Chance should be between 0 and 1. Given: "+chance);
		return ThreadLocalRandom.current().nextFloat() < chance;
	}

	public <T> T randomEntry(List<T> list)
	{
		return list.get(randomInt(list.size()));
	}
}
