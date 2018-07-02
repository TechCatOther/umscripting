package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldServer;
import org.ultramine.mods.scripting.event.IAliasedEventRegistry;
import org.ultramine.mods.scripting.event.proxy.entity.PlayerEventRegistry;
import org.ultramine.mods.scripting.mcutil.BlockPos;
import org.ultramine.mods.scripting.mcutil.internal.TempPlayerData;
import org.ultramine.server.Teleporter;
import org.ultramine.server.util.InventoryUtil;
import org.ultramine.server.util.MinecraftUtil;
import org.ultramine.server.util.WarpLocation;
import org.ultramine.server.world.WorldDescriptor;

public class PlayerMethodExtension
{
	public static IAliasedEventRegistry getOn(EntityPlayerMP self)
	{
		return new PlayerEventRegistry(self);
	}

	public static String getName(EntityPlayerMP self)
	{
		return self.getGameProfile().getName();
	}

	public static void message(EntityPlayerMP self, IChatComponent msg)
	{
		self.addChatMessage(msg);
	}

	public static void message(EntityPlayerMP self, String msg)
	{
		self.addChatMessage(new ChatComponentText(msg));
	}

	public static void message(EntityPlayerMP self, IChatComponent... msgs)
	{
		for(IChatComponent msg : msgs)
			self.addChatMessage(msg);
	}

	public static void message(EntityPlayerMP self, String... msgs)
	{
		for(String msg : msgs)
			self.addChatMessage(new ChatComponentText(msg));
	}

	public static ChatComponentStyle getDisplayName(EntityPlayerMP self)
	{
		return (ChatComponentStyle)self.func_145748_c_();
	}

	public static void teleport(EntityPlayerMP self, double x, double y, double z)
	{
		self.playerNetServerHandler.setPlayerLocation(x, y, z, self.rotationYaw, self.rotationPitch);
	}

	public static void teleport(EntityPlayerMP self, int x, int y, int z)
	{
		self.playerNetServerHandler.setPlayerLocation(x+0.5d, y, z+0.5d, self.rotationYaw, self.rotationPitch);
	}

	public static void teleport(EntityPlayerMP self, int dim, double x, double y, double z)
	{
		Teleporter.tpNow(self, dim, x, y, z);
	}

	public static void teleport(EntityPlayerMP self, int dim, int x, int y, int z)
	{
		teleport(self, dim, x + 0.5d, y, z + 0.5d);
	}

	public static void teleport(EntityPlayerMP self, int dim, double x, double y, double z, float yaw, float pitch)
	{
		teleport(self, new WarpLocation(dim, x, y, z, yaw, pitch));
	}

	public static void teleport(EntityPlayerMP self, int dim, int x, int y, int z, float yaw, float pitch)
	{
		teleport(self, new WarpLocation(dim, x + 0.5d, y, z + 0.5d, yaw, pitch));
	}

	public static void teleport(EntityPlayerMP self, WorldServer world, double x, double y, double z)
	{
		Teleporter.tpNow(self, world.provider.dimensionId, x, y, z);
	}

	public static void teleport(EntityPlayerMP self, WorldServer world, int x, int y, int z)
	{
		Teleporter.tpNow(self, world.provider.dimensionId, x+0.5d, y, z+0.5d);
	}

	public static void teleport(EntityPlayerMP self, WorldServer world, double x, double y, double z, float yaw, float pitch)
	{
		teleport(self, new WarpLocation(world.provider.dimensionId, x, y, z, yaw, pitch));
	}

	public static void teleport(EntityPlayerMP self, WorldServer world, int x, int y, int z, float yaw, float pitch)
	{
		teleport(self, new WarpLocation(world.provider.dimensionId, x + 0.5d, y, z + 0.5d, yaw, pitch));
	}

	public static void teleport(EntityPlayerMP self, WorldDescriptor world, double x, double y, double z)
	{
		Teleporter.tpNow(self, world.getDimension(), x, y, z);
	}

	public static void teleport(EntityPlayerMP self, WorldDescriptor world, int x, int y, int z)
	{
		Teleporter.tpNow(self, world.getDimension(), x+0.5d, y, z+0.5d);
	}

	public static void teleport(EntityPlayerMP self, WorldDescriptor world, int x, int y, int z, float yaw, float pitch)
	{
		teleport(self, new WarpLocation(world.getDimension(), x + 0.5d, y, z + 0.5d, yaw, pitch));
	}

	public static void teleport(EntityPlayerMP self, WorldDescriptor world, double x, double y, double z, float yaw, float pitch)
	{
		teleport(self, new WarpLocation(world.getDimension(), x, y, z, yaw, pitch));
	}

	public static void teleport(EntityPlayerMP self, WarpLocation loc)
	{
		Teleporter.tpNow(self, loc);
	}

	public static void teleport(EntityPlayerMP self, EntityPlayerMP other)
	{
		Teleporter.tpNow(self, other);
	}

	public static void syncLocation(EntityPlayerMP self)
	{
		self.playerNetServerHandler.setPlayerLocation(self.posX, self.posY, self.posZ, self.rotationYaw, self.rotationPitch);
	}

	public static void syncInventory(EntityPlayerMP self)
	{
		MinecraftServer.getServer().getConfigurationManager().syncPlayerInventory(self);
	}

	public static void kill(EntityPlayerMP self)
	{
		self.attackEntityFrom(DamageSource.command, 100000F);
	}

	public static void kick(EntityPlayerMP self, String message)
	{
		self.playerNetServerHandler.kickPlayerFromServer(message);
	}

	public static void kick(EntityPlayerMP self, IChatComponent message)
	{
		NetHandlerPlayServer net = self.playerNetServerHandler;
		net.netManager.scheduleOutboundPacket(new S40PacketDisconnect(message), v -> net.netManager.closeChannel(message));
		net.netManager.disableAutoRead();
	}

	public static void stash(EntityPlayerMP self)
	{
		self.getData().get(TempPlayerData.class).stashData(self);
	}

	public static void unstash(EntityPlayerMP self)
	{
		self.getData().get(TempPlayerData.class).unstashData(self);
	}

	public static BlockPos getBlockPos(EntityPlayerMP self)
	{
		return new BlockPos(MathHelper.floor_double(self.posX), MathHelper.floor_double(self.posY), MathHelper.floor_double(self.posZ));
	}

	public static void addItem(EntityPlayerMP self, ItemStack... stacks)
	{
		for(ItemStack is : stacks)
			InventoryUtil.addItem(self, is);
	}

	public static void addItem(EntityPlayerMP self, ItemStack stack)
	{
		InventoryUtil.addItem(self, stack);
	}

	public static void addItem(EntityPlayerMP self, Item item)
	{
		InventoryUtil.addItem(self, new ItemStack(item, 0));
	}

	public static void addItem(EntityPlayerMP self, Block block)
	{
		InventoryUtil.addItem(self, new ItemStack(block, 0));
	}

	public static void addItemToHand(EntityPlayerMP self, ItemStack stack)
	{
		self.inventory.currentItem = 0;
		int ci = self.inventory.currentItem;
		ItemStack last = self.inventory.getStackInSlotOnClosing(ci);
		self.inventory.setInventorySlotContents(ci, stack);
		if(last != null)
			addItem(self, last);
		syncInventory(self);
	}

	public static MovingObjectPosition getSelectedObject(EntityPlayerMP self)
	{
		return MinecraftUtil.getMovingObjectPosition(self);
	}

	public static NBTTagCompound getExt(EntityPlayerMP self)
	{
		return self.getData().get(TempPlayerData.class).getExtNBT();
	}
}
