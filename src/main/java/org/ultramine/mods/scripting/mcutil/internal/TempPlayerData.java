package org.ultramine.mods.scripting.mcutil.internal;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings;
import org.ultramine.server.data.player.PlayerData;
import org.ultramine.server.data.player.PlayerDataExtension;

public class TempPlayerData extends PlayerDataExtension
{
	private NBTTagCompound stashedData;
	private WorldSettings.GameType stashedGameType;

	private NBTTagCompound extNBT = new NBTTagCompound();

	public TempPlayerData(PlayerData data)
	{
		super(data);
	}

	public void onDisconnect(EntityPlayerMP player)
	{
		unstashData(player);
	}

	public void stashData(EntityPlayerMP player)
	{
		if(stashedData == null)
		{
			stashedData = new NBTTagCompound();
			player.writeToNBT(stashedData);
			stashedGameType = player.theItemInWorldManager.getGameType();
		}

		applyIsolatedData(player, null);
		player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.setGameType(player.worldObj.getWorldInfo().getGameType());
		MinecraftServer.getServer().getConfigurationManager().syncPlayerInventory(player);
	}

	public void unstashData(EntityPlayerMP player)
	{
		if(stashedData == null)
			return;

		int lastDim = player.dimension;
		player.readFromNBT(stashedData);
		if(lastDim != player.dimension)
		{
			int toDim = player.dimension;
			player.dimension = lastDim;
			player.transferToDimension(toDim);
		}
		else
		{
			player.setPlayerHealthUpdated();
			player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		}

		player.setGameType(stashedGameType);
		stashedData = null;
		stashedGameType = null;
		MinecraftServer.getServer().getConfigurationManager().syncPlayerInventory(player);
	}

	private void applyIsolatedData(EntityPlayerMP player, NBTTagCompound nbt)
	{
		double x = player.posX;
		double y = player.posY;
		double z = player.posZ;
		float yaw = player.rotationYaw;
		float pitch = player.rotationPitch;

		player.readFromNBT(nbt != null ? nbt : new NBTTagCompound());

		player.dimension = player.worldObj.provider.dimensionId;
		player.prevPosX = player.lastTickPosX = player.posX = x;
		player.prevPosY = player.lastTickPosY = player.posY = y;
		player.prevPosZ = player.lastTickPosZ = player.posZ = z;
		player.prevRotationYaw = player.rotationYaw = yaw;
		player.prevRotationPitch = player.rotationPitch = pitch;
		player.setPlayerHealthUpdated();
	}

	public NBTTagCompound getExtNBT()
	{
		return extNBT;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if(stashedData != null)
		{
			nbt.setTag("stash", stashedData);
			nbt.setByte("stash_gm", (byte) stashedGameType.getID());
		}
		nbt.setTag("ext", extNBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey("stash"))
		{
			stashedData = nbt.getCompoundTag("stash");
			stashedGameType = WorldSettings.GameType.getByID(nbt.getByte("stash_gm"));
		}
		extNBT = nbt.getCompoundTag("ext");
	}
}
