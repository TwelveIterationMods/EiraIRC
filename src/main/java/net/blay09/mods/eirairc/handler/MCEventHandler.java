// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.event.RelayChat;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.config.settings.*;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.IRCFormatting;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCEventHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			for(IRCChannel channel : connection.getChannels()) {
				GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
				BotSettings botSettings = ConfigHelper.getBotSettings(channel);
				String name = Utils.getNickIRC(event.player, channel);
				String ircMessage = Utils.getLocalizedMessage("irc.display.mc.joinMsg", name);
				if(!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayMinecraftJoinLeave)) {
					channel.message(ircMessage);
				}
				if(channel.getTopic() != null) {
					Utils.sendLocalizedMessage(event.player, "irc.display.irc.topic", channel.getName(), channel.getTopic());
				}
				if(generalSettings.getBoolean(GeneralBooleanComponent.AutoJoin)) {
					Utils.sendUserList(event.player, connection, channel);
				}
			}
		}
	}

	@SubscribeEvent
	public void onServerCommand(CommandEvent event) {
		if(event.command instanceof CommandEmote) {
			if(event.sender instanceof EntityPlayer) {
				String emote = Utils.joinStrings(event.parameters, " ").trim();
				if(emote.length() == 0) {
					return;
				}
				String mcAlias = Utils.getNickGame((EntityPlayer) event.sender);
				IChatComponent chatComponent = new ChatComponentText("* " + mcAlias + " " + emote);
				EnumChatFormatting emoteColor = Utils.getColorFormatting(SharedGlobalConfig.theme.getColor(ThemeColorComponent.emoteTextColor));
				if(emoteColor != null) {
					chatComponent.getChatStyle().setColor(emoteColor);
				}
				Utils.addMessageToChat(chatComponent);
				if(!MinecraftServer.getServer().isSinglePlayer()) {
					relayChatServer(event.sender, emote, true, false, null);
				}
				event.setCanceled(true);
			}
		} else if(event.command instanceof CommandBroadcast) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				for(IRCChannel channel : connection.getChannels()) {
					GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
					BotSettings botSettings = ConfigHelper.getBotSettings(channel);
					String ircMessage = MessageFormat.formatMessage(botSettings.getMessageFormat().ircBroadcastMessage, channel, event.sender, Utils.joinStrings(event.parameters, " "), MessageFormat.Target.IRC, MessageFormat.Mode.Message);
					if(!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayBroadcasts)) {
						channel.message(ircMessage);
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onClientChat(String text) {
		if(text.startsWith("/")) {
			if(text.startsWith("/me") && text.length() > 3) {
				return onClientEmote(text.substring(3));
			}
			return false;
		}
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(EiraIRC.instance.getConnectionCount() > 0 && IRCCommandHandler.onChatCommand(sender, text, false)) {
			return true;
		}
		if(ClientGlobalConfig.clientBridge) {
			relayChatClient(text, false, false, null, true);
			return false;
		}
		String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return false;
		}
		String[] target = chatTarget.split("/");
		IRCConnection connection = EiraIRC.instance.getConnection(target[0]);
		if(connection != null) {
			IRCContext context;
			IChatComponent chatComponent;
			if(target[1].startsWith("#")) {
				IRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel == null) {
					return true;
				}
				context = targetChannel;
				BotSettings botSettings = ConfigHelper.getBotSettings(context);
				chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendChannelMessage, context, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Message);
			} else {
				IRCUser targetUser = connection.getUser(target[1]);
				if(targetUser == null) {
					return true;
				}
				context = targetUser;
				BotSettings botSettings = ConfigHelper.getBotSettings(context);
				chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendPrivateMessage, context, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Message);
			}
			relayChatClient(text, false, false, context, false);
			Utils.addMessageToChat(chatComponent);
		}
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onClientEmote(String text) {
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(ClientGlobalConfig.clientBridge) {
			relayChatClient(text, true, false, null, true);
			return false;
		}
		String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return false;
		}
		String[] target = chatTarget.split("/");
		IRCConnection connection = EiraIRC.instance.getConnection(target[0]);
		if(connection != null) {
			IRCContext context;
			EnumChatFormatting emoteColor;
			IChatComponent chatComponent;
			if(target[1].startsWith("#")) {
				IRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel == null) {
					return true;
				}
				context = targetChannel;
				emoteColor = Utils.getColorFormatting(ConfigHelper.getTheme(targetChannel).getColor(ThemeColorComponent.emoteTextColor));
				BotSettings botSettings = ConfigHelper.getBotSettings(context);
				chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendChannelEmote, context, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
			} else {
				IRCUser targetUser = connection.getUser(target[1]);
				if(targetUser == null) {
					return true;
				}
				context = targetUser;
				emoteColor = Utils.getColorFormatting(ConfigHelper.getTheme(targetUser).getColor(ThemeColorComponent.emoteTextColor));
				BotSettings botSettings = ConfigHelper.getBotSettings(context);
				chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendPrivateEmote, context, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
			}
			relayChatClient(text, true, false, context, false);
			if(emoteColor != null) {
				chatComponent.getChatStyle().setColor(emoteColor);
			}
			Utils.addMessageToChat(chatComponent);
		}
		return true;
	}
	
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		IChatComponent senderComponent = event.player.func_145748_c_();
		EnumChatFormatting nameColor = Utils.getColorFormattingForPlayer(event.player);
		if(nameColor != null) {
			senderComponent.getChatStyle().setColor(nameColor);
		}
		event.component = new ChatComponentTranslation("chat.type.text", senderComponent, event.message);
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			if(IRCCommandHandler.onChatCommand(event.player, event.message, true)) {
				event.setCanceled(true);
				return;
			}
			relayChatServer(event.player, event.message, false, false, null);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void relayChatClient(RelayChat event) {
		relayChatClient(event.message, event.isEmote, event.isNotice, event.target, ClientGlobalConfig.clientBridge);
	}

	private void relayChatClient(String message, boolean isEmote, boolean isNotice, IRCContext target, boolean clientBridge) {
		if(target != null) {
			if(!ConfigHelper.getGeneralSettings(target).isReadOnly()) {
				String ircMessage = message;
				if(isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
				}
				if(isNotice) {
					target.notice(ircMessage);
				} else {
					target.message(ircMessage);
				}
			}
		} else {
			if(clientBridge) {
				String ircMessage = message;
				if(isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
				}
				if(!ClientGlobalConfig.clientBridgeMessageToken.isEmpty()) {
					ircMessage = ircMessage + " " + ClientGlobalConfig.clientBridgeMessageToken;
				}
				for(IRCConnection connection : EiraIRC.instance.getConnections()) {
					for(IRCChannel channel : connection.getChannels()) {
						if(!ConfigHelper.getGeneralSettings(channel).isReadOnly()) {
							if(isNotice) {
								channel.notice(ircMessage);
							} else {
								channel.message(ircMessage);
							}
						}
					}
				}
			} else {
				String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
				if(chatTarget == null) {
					return;
				}
				String[] targetArr = chatTarget.split("/");
				IRCConnection connection = EiraIRC.instance.getConnection(targetArr[0]);
				if(connection != null) {
					IRCContext context;
					if(targetArr[1].startsWith("#")) {
						IRCChannel targetChannel = connection.getChannel(targetArr[1]);
						if(targetChannel == null) {
							return;
						}
						context = targetChannel;
					} else {
						IRCUser targetUser = connection.getUser(targetArr[1]);
						if (targetUser == null) {
							return;
						}
						context = targetUser;
					}
					if(!ConfigHelper.getGeneralSettings(context).isReadOnly()) {
						String ircMessage = message;
						if(isEmote) {
							ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
						}
						if(isNotice) {
							context.notice(ircMessage);
						} else {
							context.message(ircMessage);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public void relayChatServer(RelayChat event) {
		relayChatServer(event.sender, event.message, event.isEmote, event.isNotice, event.target);
	}

	private void relayChatServer(ICommandSender sender, String message, boolean isEmote, boolean isNotice, IRCContext target) {
		if(target != null) {
			if(!ConfigHelper.getGeneralSettings(target).isReadOnly()) {
				String format = MessageFormat.getMessageFormat(target, isEmote);
				String ircMessage = MessageFormat.formatMessage(format, target, sender, message, MessageFormat.Target.IRC, (isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
				if(isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
				}
				if(isNotice) {
					target.notice(ircMessage);
				} else {
					target.message(ircMessage);
				}
			}
		} else {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				for(IRCChannel channel : connection.getChannels()) {
					String format = MessageFormat.getMessageFormat(channel, isEmote);
					String ircMessage = MessageFormat.formatMessage(format, channel, sender, message, MessageFormat.Target.IRC, (isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
					if(isEmote) {
						ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
					}
					if(!ConfigHelper.getGeneralSettings(channel).isReadOnly()) {
						if(isNotice) {
							channel.notice(ircMessage);
						} else {
							channel.message(ircMessage);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				for(IRCChannel channel : connection.getChannels()) {
					GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
					BotSettings botSettings = ConfigHelper.getBotSettings(channel);
					String name = Utils.getNickIRC((EntityPlayer) event.entityLiving, channel);
					String ircMessage = event.entityLiving.func_110142_aN().func_151521_b().getUnformattedText();
					ircMessage = ircMessage.replaceAll(event.entityLiving.getCommandSenderName(), name);
					ircMessage = IRCFormatting.toIRC(ircMessage, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
					if(!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayDeathMessages)) {
						channel.message(ircMessage);
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerNameFormat(PlayerEvent.NameFormat event) {
		event.displayname = Utils.getNickGame(event.entityPlayer);
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			for(IRCChannel channel : connection.getChannels()) {
				GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
				BotSettings botSettings = ConfigHelper.getBotSettings(channel);
				String name = Utils.getNickIRC(event.player, channel);
				String ircMessage = Utils.getLocalizedMessage("irc.display.mc.partMsg", name);
				if(!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayMinecraftJoinLeave)) {
					channel.message(ircMessage);
				}
			}
		}
	}

	public void onPlayerNickChange(String oldNick, String newNick) {
		String message = Utils.getLocalizedMessage("irc.display.mc.nickChange", oldNick, newNick);
		Utils.addMessageToChat(message);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			for(IRCChannel channel : connection.getChannels()) {
				if(!ConfigHelper.getGeneralSettings(channel).isReadOnly()) {
					channel.message(message);
				}
			}
		}
	}

}
