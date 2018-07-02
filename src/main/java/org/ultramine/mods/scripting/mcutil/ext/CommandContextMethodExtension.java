package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.ultramine.commands.CommandContext;

public class CommandContextMethodExtension
{
	public static EntityPlayerMP getPlayer(CommandContext self)
	{
		return self.getSenderAsPlayer();
	}

	public static boolean hasPermission(CommandContext self, String perm)
	{
		return self.getSenderAsPlayer().hasPermission(perm);
	}

	public static void checkPermission(CommandContext self, String perm)
	{
		self.checkSenderPermission(perm);
	}

	public static void checkPermission(CommandContext self, String perm, String msg)
	{
		self.checkSenderPermission(perm, msg);
	}

	public static void sendMessage(CommandContext self, IChatComponent msg)
	{
		self.getSender().addChatMessage(msg);
	}

	public static void message(CommandContext self, IChatComponent msg)
	{
		sendMessage(self, msg);
	}

	public static void message(CommandContext self, String msg)
	{
		sendMessage(self, new ChatComponentText(msg));
	}
}
