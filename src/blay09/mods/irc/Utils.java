// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc;

import java.util.List;

import blay09.mods.irc.config.GlobalConfig;
import blay09.mods.irc.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class Utils {

	private static final char INVALID_COLOR = 'n';
	
	public static void addMessageToChat(String text) {
		if(MinecraftServer.getServer() != null) {
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(text);
		} else {
			Minecraft.getMinecraft().thePlayer.addChatMessage(text);
		}
	}
	
	public static String getNickFromUser(String user) {
		int i = user.indexOf("!");
		if(i == -1) {
			return user;
		}
		return user.substring(0, i);
	}
	
	public static String getAliasForPlayer(EntityPlayer player) {
		if(!GlobalConfig.enableAliases) {
			return player.username;
		}
		String name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC").getString("Alias");
		if(name.isEmpty()) {
			name = player.username;
		}
		return name;
	}
	
	public static boolean isOP(ICommandSender sender) {
		if(MinecraftServer.getServer().isSinglePlayer()) {
			return true;
		}
		if(sender instanceof EntityPlayer) {
			return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).getOps().contains(((EntityPlayer)sender).username.toLowerCase().trim());
		}
		return true;
	}
	
	public static String getColoredName(String name, char colorCode) {
		if(colorCode == INVALID_COLOR) {
			return name;
		}
		return "\u00a7" + String.valueOf(colorCode) + name + "\u00a7f";
	}
	
	public static String getColorAliasForPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC");
		String alias = null;
		if(GlobalConfig.enableAliases) {
			alias = tagCompound.getString("Alias");
			if(alias.isEmpty()) {
				alias = player.username;
			}
		} else {
			alias = player.username;
		}
		boolean isOP = isOP(player);
		if(!GlobalConfig.enableNameColors && !isOP) {
			return alias;
		}
		String colorName = tagCompound.getString("NameColor");
		if(!colorName.isEmpty()) {
			char colorCode = getColorCode(colorName);
			if(colorCode == INVALID_COLOR) {
				return alias;
			}
			return getColoredName(alias, colorCode);
		} else if(isOP) {
			if(!GlobalConfig.opColor.isEmpty()) {
				char colorCode = getColorCode(colorName);
				if(colorCode == INVALID_COLOR) {
					return alias;
				}
				return getColoredName(alias, colorCode);
			}
		}
		return alias;
	}
	
	public static boolean isValidColor(String colorName) {
		return getColorCode(colorName) != INVALID_COLOR;
	}
	
	public static char getColorCode(String colorName) {
		if(colorName.isEmpty()) {
			return INVALID_COLOR;
		}
		colorName = colorName.toLowerCase();
		char colorCode = INVALID_COLOR;
		if(colorName.equals("black")) {
			colorCode = '0';
		} else if(colorName.equals("darkblue")) {
			colorCode = '1';
		} else if(colorName.equals("green")) {
			colorCode = '2';
		} else if(colorName.equals("cyan")) {
			colorCode = '3';
		} else if(colorName.equals("darkred")) {
			colorCode = '4';
		} else if(colorName.equals("purple")) {
			colorCode = '5';
		} else if(colorName.equals("gold")) {
			colorCode = '6';
		} else if(colorName.equals("gray") || colorName.equals("grey")) {
			colorCode = '7';
		} else if(colorName.equals("darkgray") || colorName.equals("darkgrey")) {
			colorCode = '8';
		} else if(colorName.equals("blue")) {
			colorCode = '9';
		} else if(colorName.equals("lime")) {
			colorCode = 'a';
		} else if(colorName.equals("lightblue")) {
			colorCode = 'b';
		} else if(colorName.equals("red")) {
			colorCode = 'c';
		} else if(colorName.equals("magenta") || colorName.equals("pink")) {
			colorCode = 'd';
		} else if(colorName.equals("yellow")) {
			colorCode = 'e';
		} else if(colorName.equals("white")) {
			colorCode = 'f';
		}
		return colorCode;
	}

	public static String getColoredName(String nick, String colorName) {
		return getColoredName(nick, getColorCode(colorName));
	}

	public static void addValidColorsToList(List<String> list) {
		list.add("black");
		list.add("darkblue");
		list.add("green");
		list.add("cyan");
		list.add("darkred");
		list.add("purple");
		list.add("gold");
		list.add("gray");
		list.add("darkgray");
		list.add("blue");
		list.add("lime");
		list.add("lightblue");
		list.add("red");
		list.add("magenta");
		list.add("yellow");
		list.add("white");		
	}

	public static void addConnectionsToList(List<String> list) {
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			list.add(connection.getHost());
		}		
	}

	public static void addBooleansToList(List<String> list) {
		list.add("true");
		list.add("false");		
	}
	
}
