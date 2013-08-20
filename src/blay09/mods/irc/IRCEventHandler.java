// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc;

import blay09.mods.irc.config.GlobalConfig;
import blay09.mods.irc.config.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IChatListener;

public class IRCEventHandler implements IPlayerTracker {
	
	public void onIRCConnect(IRCConnection connection) {
		String mcMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.connected", connection.getHost());
		Utils.addMessageToChat(mcMessage);
	}
	
	public void onIRCDisconnect(IRCConnection connection) {
		String mcMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.disconnected", connection.getHost());
		Utils.addMessageToChat(mcMessage);
	}
	
	public void onIRCJoin(IRCConnection connection, String channel, String user, String nick) {
		if(!GlobalConfig.showIRCJoinLeave)  {
			return;
		}
		if(connection.getConfig().hasChannelFlags(channel, "rj")) {
			String mcMessage = EnumChatFormatting.YELLOW + StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.joinMsgIRC", channel, nick);
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	public void onIRCNickChange(IRCConnection connection, String oldNick, String newNick) {
		String mcMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.nickChangeIRC", connection.getHost(), oldNick, newNick);
		Utils.addMessageToChat(mcMessage);
	}
	
	public void onIRCPart(IRCConnection connection, String channel, String user, String nick) {
		if(!GlobalConfig.showIRCJoinLeave) {
			return;
		}
		if(connection.getConfig().hasChannelFlags(channel, "rj")) {
			String mcMessage = EnumChatFormatting.YELLOW + StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.partMsgIRC", channel, nick);
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	public void onIRCPrivateMessage(IRCConnection connection, String user, String nick, String message) {
		if(!GlobalConfig.allowPrivateMessages) {
			connection.sendPrivateMessage(nick, "Private Messages are disabled.");
			return;
		}
		if(connection.getConfig().allowPrivateMessages) {
			String mcMessage = "[Private] <" + Utils.getColoredName(nick, GlobalConfig.ircColor) + ">: " + message;
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	public void onIRCPrivateMessageToPlayer(IRCConnection ircConnection, String user, String nick, EntityPlayer entityPlayer, String message) {
		String mcMessage = "[Private] <" + Utils.getColoredName(nick, GlobalConfig.ircColor) + ">: " + message;
		entityPlayer.sendChatToPlayer(mcMessage);
	}

	public void onIRCPrivateEmote(IRCConnection connection, String user, String nick, String message) {
		if(!GlobalConfig.allowPrivateMessages) {
			return;
		}
		if(connection.getConfig().allowPrivateMessages) {
			String mcMessage = "[Private] * " + nick + " " + message;
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	public void onIRCMessage(IRCConnection connection, String channel, String user, String nick, String message) {
		if(connection.getConfig().hasChannelFlags(channel, "re")) {
			String mcMessage = "[" + channel + "] <" + Utils.getColoredName(nick, GlobalConfig.ircColor) + ">: " + message;
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	public void onIRCEmote(IRCConnection connection, String channel, String user, String nick, String message) {
		if(connection.getConfig().hasChannelFlags(channel, "re")) {
			String mcMessage = "[" + channel + "] * " + nick + " " + message;
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if(!GlobalConfig.showMinecraftJoinLeave) {
			return;
		}
		String name = Utils.getAliasForPlayer(player);
		String ircMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.joinMsgMC", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			connection.broadcastMessage(ircMessage, "wJ");
		}
	}

	@ForgeSubscribe
	public void onCommand(CommandEvent event) {
		if(event.command.getCommandName().equals("me")) {
			if(event.sender instanceof EntityPlayer) {
				String emote = "";
				for(int i = 0; i < event.parameters.length; i++) {
					emote += " " + event.parameters[i];
				}
				emote = emote.trim();
				if(emote.length() == 0) {
					return;
				}
				String message = "* " + Utils.getAliasForPlayer((EntityPlayer) event.sender) + emote;
				Utils.addMessageToChat(message);
				if(!MinecraftServer.getServer().isSinglePlayer()) {
					for(IRCConnection connection : EiraIRC.instance.getConnections()) {
						connection.broadcastMessage(message, "wE");
					}
				}
				event.setCanceled(true);
			}
		}
	}

	public boolean onClientChat(String text) {
		EnumChatTarget chatTarget = EiraIRC.instance.getChatTarget();
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			String target = EiraIRC.instance.getTargetChannel();
			String[] channel = target.split(":");
			IRCConnection connection = EiraIRC.instance.getConnection(channel[0]);
			if(connection != null) {
				if(channel[1].startsWith("#")) {
					connection.sendChannelMessage(channel[1], text);
				} else {
					connection.sendPrivateMessage(channel[1], text);
				}
				String mcMessage = "[" + channel[1] + "] <" + Minecraft.getMinecraft().thePlayer.username + "> " + text;
				Utils.addMessageToChat(mcMessage);
			}
			return false;
		}
		boolean doMC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.MinecraftOnly;
		boolean doIRC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.IRCOnly;
		if(!doMC) {
			Utils.addMessageToChat("[IRC] <" + Minecraft.getMinecraft().thePlayer.username + "> " + text);
		}
		if(doIRC) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.broadcastMessage(text, "w");
			}			
		}
		return doMC;
	}
	
	public boolean onClientEmote(String text) {
		EnumChatTarget chatTarget = EiraIRC.instance.getChatTarget();
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			String target = EiraIRC.instance.getTargetChannel();
			String[] channel = target.split(":");
			IRCConnection connection = EiraIRC.instance.getConnection(channel[0]);
			if(connection != null) {
				connection.sendChannelMessage(channel[1], "ACTION" + text + "");
				Utils.addMessageToChat("[" + channel[1] + "] * " + Minecraft.getMinecraft().thePlayer.username + text);
			}
			return false;
		}
		boolean doMC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.MinecraftOnly;
		boolean doIRC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.IRCOnly;
		if(!doMC) {
			Utils.addMessageToChat("[IRC] * " + Minecraft.getMinecraft().thePlayer.username + text);
		}
		if(doIRC) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.broadcastMessage("ACTION" + text + "", "wE");
			}			
		}
		return doMC;
	}
	
	@ForgeSubscribe
	public void onServerChat(ServerChatEvent event) {
		String name = Utils.getColorAliasForPlayer(event.player);
		event.line = "<" + name + "> " + event.message;
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			String ircMessage = "<" + Utils.getAliasForPlayer(event.player) + "> " + event.message;
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.broadcastMessage(ircMessage, "w");
			}
		}
	}
	
	@ForgeSubscribe
	public void onPlayerDeath(LivingDeathEvent event) {
		if(!GlobalConfig.showDeathMessages) {
			return;
		}
		if(event.entityLiving instanceof EntityPlayer) {
			String name = Utils.getAliasForPlayer((EntityPlayer) event.entityLiving);
			String ircMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.deathMsgMC", name, event.source.damageType);
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.broadcastMessage(ircMessage, "wD");
			}
		}
	}
	
	@Override
	public void onPlayerLogout(EntityPlayer player) {
		if(!GlobalConfig.showMinecraftJoinLeave) {
			return;
		}
		String name = Utils.getAliasForPlayer(player);
		String ircMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.partMsgMC", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			connection.broadcastMessage(ircMessage, "wJ");
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}

	public void onPlayerNickChange(String oldNick, String newNick) {
		String message = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.nickChangeMC", oldNick, newNick);
		Utils.addMessageToChat(message);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			connection.broadcastMessage(message, "w");
		}
	}

}
