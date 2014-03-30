// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.handler;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.DisplayConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IIRCEventHandler;
import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.irc.IRCUser;
import blay09.mods.eirairc.util.ConfigHelper;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.NotificationType;
import blay09.mods.eirairc.util.Utils;

public class IRCEventHandler implements IIRCEventHandler {

	@Override
	public void onNickChange(IRCConnection connection, IRCUser user, String nick) {
		if(!DisplayConfig.relayNickChanges) {
			return;
		}
		String mcMessage = Utils.getLocalizedMessage("irc.display.irc.nickChange", connection.getHost(), user.getName(), nick);
		Utils.addMessageToChat(mcMessage);
	}

	@Override
	public void onUserJoin(IRCConnection connection, IRCUser user, IRCChannel channel) {
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(DisplayConfig.relayIRCJoinLeave && !channelConfig.isMuted() && channelConfig.relayIRCJoinLeave) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.joinMsg", channel.getName(), user.getName());
			Utils.addMessageToChat(mcMessage);
		}
		if(channelConfig.isAutoWho()) {
			Utils.sendUserList(connection, user);
		}
	}

	@Override
	public void onUserPart(IRCConnection connection, IRCUser user, IRCChannel channel, String quitMessage) {
		if(!DisplayConfig.relayIRCJoinLeave) {
			return;
		}
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted() && channelConfig.relayIRCJoinLeave) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.partMsg", channel.getName(), user.getName());
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onUserQuit(IRCConnection connection, IRCUser user, String quitMessage) {
		if(!DisplayConfig.relayIRCJoinLeave) {
			return;
		}
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		boolean hasFlags = false;
		for(IRCChannel channel : user.getChannels()) {
			ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
			if(!channelConfig.isMuted() && channelConfig.relayIRCJoinLeave) {
				hasFlags = true;
				break;
			}
		}
		if(hasFlags) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.quitMsg", connection.getHost(), user.getName(), quitMessage);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onPrivateEmote(IRCConnection connection, IRCUser user, String message) {
		if(!GlobalConfig.allowPrivateMessages) {
			return;
		}
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		if(serverConfig.allowsPrivateMessages()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			message = Utils.filterCodes(message);
			String emoteColor = ConfigHelper.getEmoteColor(serverConfig);
			String mcMessage = (emoteColor != null ? Globals.COLOR_CODE_PREFIX + Utils.getColorCode(emoteColor) : "") + Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcPrivateEmote, connection, user.getIdentifier(), user.getName(), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onPrivateMessage(IRCConnection connection, IRCUser user, String message) {
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		if(!serverConfig.isClientSide()) {
			onIRCBotPrivateCommand(connection, user, message);
			return;
		}
		if(!GlobalConfig.allowPrivateMessages) {
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.msg.disabled"));
			return;
		}
		if(serverConfig.getHost().equals(Globals.TWITCH_SERVER)) {
			if(user.getName().equals("jtv")) {
				// Ignore messages from Twitch bot for now
				return;
			}
		}
		if(serverConfig.allowsPrivateMessages()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			message = Utils.filterCodes(message);
			String mcMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcPrivateMessage, connection, user.getIdentifier(), Utils.getColoredName(user.getName(), ConfigHelper.getIRCColor(serverConfig)), message);
			Utils.addMessageToChat(mcMessage);
			String notifyMsg = mcMessage;
			if(notifyMsg.length() > 42) {
				notifyMsg = notifyMsg.substring(0, 42) + "...";
			}
			EiraIRC.proxy.publishNotification(NotificationType.PrivateMessage, notifyMsg);
			EiraIRC.instance.getChatSessionHandler().addTargetUser(user);
		} else {
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.msg.disabled"));
		}
	}

	@Override
	public void onChannelEmote(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			message = Utils.filterCodes(message);
			String emoteColor = ConfigHelper.getEmoteColor(channelConfig);
			String mcMessage = (emoteColor != null ? Globals.COLOR_CODE_PREFIX + Utils.getColorCode(emoteColor) : "") + Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcChannelEmote, connection.getHost(), channel.getName(), user.getIdentifier(), user.getName(), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onChannelMessage(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		if(onIRCBotCommand(connection, channel, user, message)) {
			return;
		}
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			message = Utils.filterCodes(message);
			String mcMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcChannelMessage, connection.getHost(), channel.getName(), user.getIdentifier(), Utils.getColoredName(user.getName(), ConfigHelper.getIRCColor(channelConfig)), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	private boolean onIRCBotCommand(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		if(Utils.getServerConfig(connection).isClientSide()) {
			return false;
		}
		if(message.equals("!auth")) {
			connection.whois(user.getName());
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.auth"));
			return true;
		}
		if(message.equals("!who")) {
			Utils.sendUserList(connection, user);
			return true;
		}
		if(message.equals("!help")) {
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.cmdlist"));
			return true;
		}
		if(message.startsWith("!op")) {
			if(!GlobalConfig.interOpAuthList.contains(user.getAuthLogin())) {
				connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.noPermission"));
				return true;
			}
			if(message.length() < 4) {
				connection.sendPrivateNotice(user, "Usage: !op <command>");
				return true;
			}
			String cmd = message.substring(4);
			String result = MinecraftServer.getServer().executeCommand(cmd);
			connection.sendPrivateNotice(user, "> " + result);
			return true;
		}
		return false;
	}
	
	private void onIRCBotPrivateCommand(IRCConnection connection, IRCUser user, String message) {
		String lmessage = message.toLowerCase();
		if(lmessage.equals("help")) {
			connection.sendPrivateNotice(user, "***** EiraIRC Help *****");
			connection.sendPrivateNotice(user, "EiraIRC connects a Minecraft client or a whole server");
			connection.sendPrivateNotice(user, "to one or multiple IRC channels and servers.");
			connection.sendPrivateNotice(user, "Visit http://blay09.net/?page_id=63 for more information on this bot.");
			connection.sendPrivateNotice(user, " ");
			connection.sendPrivateNotice(user, "The following commands are available:");
			connection.sendPrivateNotice(user, "HELP            Prints this command list");
			connection.sendPrivateNotice(user, "WHO            Prints out a list of all players online");
			connection.sendPrivateNotice(user, "ALIAS            Look up the username of an online player");
			connection.sendPrivateNotice(user, "MSG            Send a private message to an online player");
			connection.sendPrivateNotice(user, "OP            Perform an OP-command on the server (requires permissions)");
			connection.sendPrivateNotice(user, "***** End of Help *****");
		} else if(lmessage.equals("who")) {
			Utils.sendUserList(connection, user);
		} else if(lmessage.equals("auth")) {
			connection.whois(user.getName());
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.auth"));
		} else if(lmessage.startsWith("alias")) {
			if(!GlobalConfig.enableAliases) {
				connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.alias.disabled"));
				return;
			}
			int i = message.indexOf(" ", 7);
			String alias = message.substring(7);
			List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for(EntityPlayer entity : playerEntityList) {
				if(Utils.getAliasForPlayer(entity).equals(alias)) {
					connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.alias.lookup", alias, entity.username));
					return;
				}
			}
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
		} else if(lmessage.startsWith("msg")) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			if(!GlobalConfig.allowPrivateMessages || !serverConfig.allowsPrivateMessages()) {
				connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.msg.disabled"));
				return;
			}
			int i = message.indexOf(" ", 5);
			String playerName = message.substring(4, i);
			EntityPlayer entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
			if(entityPlayer == null) {
				List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
				for(EntityPlayer entity : playerEntityList) {
					if(Utils.getAliasForPlayer(entity).equals(playerName)) {
						entityPlayer = entity;
					}
				}
				if(entityPlayer == null) {
					connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
					return;
				}
			}
			String targetMessage = message.substring(i + 1);
			EiraIRC.instance.getIRCEventHandler().onIRCPrivateMessageToPlayer(connection, user, entityPlayer, targetMessage);
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.msgSent", playerName, targetMessage));
			return;
		} else if(lmessage.startsWith("op")) {
			if(!GlobalConfig.interOpAuthList.contains(user.getAuthLogin())) {
				connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.noPermission"));
				return;
			}
			if(message.length() < 3) {
				connection.sendPrivateNotice(user, "Usage: !op <command>");
				return;
			}
			String cmd = message.substring(3);
			String result = MinecraftServer.getServer().executeCommand(cmd);
			connection.sendPrivateNotice(user, "> " + result);
			return;
		} else {
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.unknownCommand"));
		}
	}
	
	private void onIRCPrivateMessageToPlayer(IRCConnection connection, IRCUser user, EntityPlayer entityPlayer, String message) {
		if(GlobalConfig.enableLinkFilter) {
			message = Utils.filterLinks(message);
		}
		message = Utils.filterCodes(message);
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
		String mcMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcPrivateMessage, connection, user.getIdentifier(), Utils.getColoredName(user.getName(), ConfigHelper.getIRCColor(serverConfig)), message);
		entityPlayer.sendChatToPlayer(Utils.getUnlocalizedChatMessage(mcMessage));
		String notifyMsg = mcMessage;
		if(notifyMsg.length() > 42) {
			notifyMsg = notifyMsg.substring(0, 42) + "...";
		}
		EiraIRC.proxy.sendNotification((EntityPlayerMP) entityPlayer, NotificationType.PrivateMessage, notifyMsg);
	}

	@Override
	public void onTopicChange(IRCChannel channel, String topic) {
		String mcMessage = Utils.getLocalizedMessage("irc.display.irc.topic", channel.getName(), channel.getTopic());
		Utils.addMessageToChat(mcMessage);
	}

}
