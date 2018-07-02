package org.ultramine.mods.scripting.mcutil.ext;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatMessagesMethodExtension
{
	public static Object asType(String self, Class<?> cls)
	{
		if(IChatComponent.class.isAssignableFrom(cls))
			return new ChatComponentText(self);
		return null;
	}

	public static ChatComponentStyle add(ChatComponentStyle self, IChatComponent other)
	{
		self.appendSibling(other);
		return self;
	}

	public static ChatComponentStyle add(ChatComponentStyle self, String text)
	{
		ChatComponentText other = new ChatComponentText(text);
		self.appendSibling(other);
		return self;
	}

	public static ChatComponentStyle add(String self, IChatComponent other)
	{
		ChatComponentText main = new ChatComponentText(self);
		main.appendSibling(other);
		return main;
	}

	public static ChatComponentStyle plus(ChatComponentStyle self, IChatComponent other)
	{
		return add(self, other);
	}

	public static ChatComponentStyle plus(ChatComponentStyle self, String text)
	{
		return add(self, text);
	}

	public static ChatComponentStyle plus(String self, IChatComponent other)
	{
		return add(self, other);
	}

	public static ChatComponentStyle plus(ChatComponentStyle self, Object text)
	{
		return add(self, text.toString());
	}

	//style

	public static ChatComponentStyle color(ChatComponentStyle self, EnumChatFormatting color)
	{
		self.getChatStyle().setColor(color);
		return self;
	}

	public static ChatComponentStyle color(String self, EnumChatFormatting color)
	{
		return color(new ChatComponentText(self), color);
	}

	public static ChatComponentStyle bold(ChatComponentStyle self)
	{
		self.getChatStyle().setBold(true);
		return self;
	}

	public static ChatComponentStyle bold(String self)
	{
		return bold(new ChatComponentText(self));
	}

	public static ChatComponentStyle italic(ChatComponentStyle self)
	{
		self.getChatStyle().setItalic(true);
		return self;
	}

	public static ChatComponentStyle italic(String self)
	{
		return italic(new ChatComponentText(self));
	}

	public static ChatComponentStyle underlined(ChatComponentStyle self)
	{
		self.getChatStyle().setUnderlined(true);
		return self;
	}

	public static ChatComponentStyle underlined(String self)
	{
		return underlined(new ChatComponentText(self));
	}

	public static ChatComponentStyle strikethrough(ChatComponentStyle self)
	{
		self.getChatStyle().setStrikethrough(true);
		return self;
	}

	public static ChatComponentStyle strikethrough(String self)
	{
		return strikethrough(new ChatComponentText(self));
	}

	public static ChatComponentStyle obfuscated(ChatComponentStyle self)
	{
		self.getChatStyle().setObfuscated(true);
		return self;
	}

	public static ChatComponentStyle obfuscated(String self)
	{
		return obfuscated(new ChatComponentText(self));
	}

	//Click event

	public static ChatComponentStyle openURL(ChatComponentStyle self, String arg)
	{
		self.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, arg));
		return self;
	}

	public static ChatComponentStyle openURL(String self, String arg)
	{
		return openURL(new ChatComponentText(self), arg);
	}

	public static ChatComponentStyle exec(ChatComponentStyle self, String arg)
	{
		self.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, arg));
		return self;
	}

	public static ChatComponentStyle exec(String self, String arg)
	{
		return exec(new ChatComponentText(self), arg);
	}

	public static ChatComponentStyle suggest(ChatComponentStyle self, String arg)
	{
		self.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, arg));
		return self;
	}

	public static ChatComponentStyle suggest(String self, String arg)
	{
		return suggest(new ChatComponentText(self), arg);
	}

	//Hover events

	public static ChatComponentStyle tooltip(ChatComponentStyle self, ChatComponentText arg)
	{
		self.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, arg));
		return self;
	}

	public static ChatComponentStyle tooltip(String self, ChatComponentText arg)
	{
		return tooltip(new ChatComponentText(self), arg);
	}

	public static ChatComponentStyle tooltip(ChatComponentStyle self, String arg)
	{
		self.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(arg)));
		return self;
	}

	public static ChatComponentStyle tooltip(String self, String arg)
	{
		return tooltip(new ChatComponentText(self), arg);
	}

	//direct colors

	public static ChatComponentStyle black(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.BLACK);
		return self;
	}

	public static ChatComponentStyle black(String self)
	{
		return black(new ChatComponentText(self));
	}

	public static ChatComponentStyle darkBlue(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.DARK_BLUE);
		return self;
	}

	public static ChatComponentStyle darkBlue(String self)
	{
		return darkBlue(new ChatComponentText(self));
	}

	public static ChatComponentStyle darkGreen(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
		return self;
	}

	public static ChatComponentStyle darkGreen(String self)
	{
		return darkGreen(new ChatComponentText(self));
	}

	public static ChatComponentStyle darkAqua(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.DARK_AQUA);
		return self;
	}

	public static ChatComponentStyle darkAqua(String self)
	{
		return darkAqua(new ChatComponentText(self));
	}

	public static ChatComponentStyle darkRed(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
		return self;
	}

	public static ChatComponentStyle darkRed(String self)
	{
		return darkRed(new ChatComponentText(self));
	}

	public static ChatComponentStyle darkPurple(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.DARK_PURPLE);
		return self;
	}

	public static ChatComponentStyle darkPurple(String self)
	{
		return darkPurple(new ChatComponentText(self));
	}

	public static ChatComponentStyle gold(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.GOLD);
		return self;
	}

	public static ChatComponentStyle gold(String self)
	{
		return gold(new ChatComponentText(self));
	}

	public static ChatComponentStyle gray(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.GRAY);
		return self;
	}

	public static ChatComponentStyle gray(String self)
	{
		return gray(new ChatComponentText(self));
	}

	public static ChatComponentStyle darkGray(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.DARK_GRAY);
		return self;
	}

	public static ChatComponentStyle darkGray(String self)
	{
		return darkGray(new ChatComponentText(self));
	}

	public static ChatComponentStyle blue(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.BLUE);
		return self;
	}

	public static ChatComponentStyle blue(String self)
	{
		return blue(new ChatComponentText(self));
	}

	public static ChatComponentStyle green(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.GREEN);
		return self;
	}

	public static ChatComponentStyle green(String self)
	{
		return green(new ChatComponentText(self));
	}

	public static ChatComponentStyle aqua(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.AQUA);
		return self;
	}

	public static ChatComponentStyle aqua(String self)
	{
		return aqua(new ChatComponentText(self));
	}

	public static ChatComponentStyle red(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.RED);
		return self;
	}

	public static ChatComponentStyle red(String self)
	{
		return red(new ChatComponentText(self));
	}

	public static ChatComponentStyle lightPurple(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
		return self;
	}

	public static ChatComponentStyle lightPurple(String self)
	{
		return lightPurple(new ChatComponentText(self));
	}

	public static ChatComponentStyle yellow(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.YELLOW);
		return self;
	}

	public static ChatComponentStyle yellow(String self)
	{
		return yellow(new ChatComponentText(self));
	}

	public static ChatComponentStyle white(ChatComponentStyle self)
	{
		self.getChatStyle().setColor(EnumChatFormatting.WHITE);
		return self;
	}

	public static ChatComponentStyle white(String self)
	{
		return white(new ChatComponentText(self));
	}
}
