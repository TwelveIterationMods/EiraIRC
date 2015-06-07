package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.addon.Compatibility;
import net.blay09.mods.eirairc.addon.EiraMoticonsAddon;
import net.blay09.mods.eirairc.api.event.ApplyEmoticons;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.*;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageFormat {

	public enum Target {
		IRC,
		Minecraft
	}

	public enum Mode {
		Message,
		Emote
	}

	private static final Pattern playerTagPattern = Pattern.compile("[\\[][^\\]]+[\\]]");
	public static final Pattern urlPattern = Pattern.compile("(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?");
	public static final Pattern namePattern = Pattern.compile("@([^ ]+)");

	public static String getMessageFormat(IRCContext context, boolean isEmote) {
		BotSettings botSettings = ConfigHelper.getBotSettings(context);
		if (context instanceof IRCUser) {
			if (isEmote) {
				return botSettings.getMessageFormat().ircPrivateEmote;
			} else {
				return botSettings.getMessageFormat().ircPrivateMessage;
			}
		} else {
			if (isEmote) {
				return botSettings.getMessageFormat().ircChannelEmote;
			} else {
				return botSettings.getMessageFormat().ircChannelMessage;
			}
		}
	}

	public static IChatComponent createChatComponentForMessage(String message) {
		IChatComponent rootComponent = new ChatComponentText("");
		StringBuilder buffer = new StringBuilder();
		Matcher urlMatcher = urlPattern.matcher(message);
		Matcher nameMatcher = namePattern.matcher(message);
		int currentIndex = 0;
		while(currentIndex < message.length()) {
			// Find the next word in the message
			int nextWhitespace = message.indexOf(' ', currentIndex);
			if(nextWhitespace == -1) {
				nextWhitespace = message.length();
			}
			// Update Matchers to check the correct region
			urlMatcher.region(currentIndex, nextWhitespace);
			nameMatcher.region(currentIndex, nextWhitespace);
			if(urlMatcher.matches()) {
				// Flush the buffer
				if(buffer.length() > 0) {
					rootComponent = appendTextToRoot(rootComponent, buffer.toString());
					buffer = new StringBuilder();
				}
				// Create URL component
				String urlText = urlMatcher.group();
				ChatComponentText urlComponent = new ChatComponentText(urlText);
				// Make sure a protocol is specified for the ClickEvent value to prevent NPE in GuiChat (getScheme())
				if(!urlText.startsWith("http://") && !urlText.startsWith("https://")) {
					urlText = "http://" + urlText;
				}
				urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlText));
				rootComponent = appendSiblingToRoot(rootComponent, urlComponent);
				currentIndex = nextWhitespace;
			} else if(nameMatcher.matches()) {
				// Flush the buffer
				if(buffer.length() > 0) {
					rootComponent = appendTextToRoot(rootComponent, buffer.toString());
					buffer = new StringBuilder();
				}
				// Create Name Component
				String nameText = nameMatcher.group();
				ChatComponentText nameComponent = new ChatComponentText(nameText);
				nameComponent.getChatStyle().setItalic(true);
				nameComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, nameText + " "));
				rootComponent = appendSiblingToRoot(rootComponent, nameComponent);
				currentIndex = nextWhitespace;
			} else {
				buffer.append(message.substring(currentIndex, Math.min(message.length(), nextWhitespace + 1)));
				currentIndex = nextWhitespace + 1;
			}
		}
		// Flush the buffer
		if(buffer.length() > 0) {
			rootComponent = appendTextToRoot(rootComponent, buffer.toString());
		}
		return rootComponent;
	}

	private static IChatComponent appendSiblingToRoot(IChatComponent root, IChatComponent sibling) {
		root.appendSibling(sibling);
		return root;
	}

	private static IChatComponent appendTextToRoot(IChatComponent root, String text) {
		ApplyEmoticons emoticons = new ApplyEmoticons(new ChatComponentText(text));
		MinecraftForge.EVENT_BUS.post(emoticons);
		root.appendSibling(emoticons.component);
		return root;
	}

	public static String filterLinks(String message) {
		String[] s = message.split(" ");
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.length; i++) {
			Matcher matcher = urlPattern.matcher(s[i]);
			if(i > 0) {
				sb.append(" ");
			}
			sb.append(matcher.replaceAll(Utils.getLocalizedMessage("irc.general.linkRemoved")));
		}
		return sb.toString();
	}

	public static String filterPlayerTags(String playerName) {
		return playerTagPattern.matcher(playerName).replaceAll("");
	}

	private static String filterAllowedCharacters(String message) {
		StringBuilder sb = new StringBuilder();
		char[] charArray = message.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (isAllowedCharacter(charArray[i])) {
				sb.append(charArray[i]);
			}
		}
		return sb.toString();
	}

	public static String formatNick(String nick, IRCContext context, Target target, Mode mode, IRCUser ircUser) {
		if(target == Target.IRC) {
			if(SharedGlobalConfig.hidePlayerTags) {
				nick = filterPlayerTags(nick);
			}
			nick = String.format(ConfigHelper.getBotSettings(context).getString(BotStringComponent.NickFormat), nick);
			if(SharedGlobalConfig.preventUserPing) {
				nick = nick.substring(0, 1) + '\u0081' + nick.substring(1);
			}
		} else if(target == Target.Minecraft && context instanceof IRCChannel) {
			GeneralSettings settings = ConfigHelper.getGeneralSettings(context);
			if(settings.getBoolean(GeneralBooleanComponent.ShowNameFlags)) {
				nick = ircUser.getChannelModePrefix((IRCChannel) context) + nick;
			}
			if(Compatibility.eiraMoticonsInstalled && SharedGlobalConfig.twitchNameBadges) {
				if(context.getConnection().getHost().equals(Globals.TWITCH_SERVER)) {
					if(ircUser.getName().toLowerCase().equals(context.getName().substring(1).toLowerCase())) {
						nick = EiraMoticonsAddon.casterBadge.getChatString() + " " + nick;
					} else if(ircUser.isOperator((IRCChannel) context)) {
						nick = EiraMoticonsAddon.modBadge.getChatString() + " " + nick;
					}
					if(((IRCUserImpl) ircUser).isSubscriber()) {
						String badgeString = EiraMoticonsAddon.getSubscriberBadgeString((IRCChannel) context);
						if(!badgeString.isEmpty()) {
							nick = badgeString + " " + nick;
						}
					}
				}
			}
		}
		return nick;
	}

	public static String formatMessage(String format, IRCContext context, ICommandSender sender, String message, Target target, Mode mode) {
		String result = formatChatComponent(format, context, sender, message, target, mode).getUnformattedText();
		BotSettings botSettings = ConfigHelper.getBotSettings(context);
		if(target == Target.IRC) {
			result = IRCFormatting.toIRC(result, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
		} else if(target == Target.Minecraft) {
			result = IRCFormatting.toMC(result, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
			result = filterAllowedCharacters(result);
		}
		return result;
	}

	public static IChatComponent formatChatComponent(String format, IRCContext context, ICommandSender sender, String message, Target target, Mode mode) {
		IChatComponent root = new ChatComponentText("");
		EnumChatFormatting nextColor = null;
		StringBuilder sb = new StringBuilder();
		int currentIdx = 0;
		while(currentIdx < format.length()) {
			char c = format.charAt(currentIdx);
			if(c == '{') {
				int tokenEnd = format.indexOf('}', currentIdx);
				if(tokenEnd != -1) {
					boolean validToken = true;
					String token = format.substring(currentIdx + 1, tokenEnd);
					IChatComponent component = null;
					if(token.equals("SERVER")) {
						component = new ChatComponentText(Utils.getCurrentServerName());
					} else if(token.equals("USER")) {
						component = new ChatComponentText(sender.getCommandSenderName());
					} else if(token.equals("CHANNEL")) {
						component = new ChatComponentText(context != null ? context.getName() : "");
					} else if(token.equals("NICK")) {
						if(sender instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) sender;
							component = player.getFormattedCommandSenderName().createCopy();
							String displayName = component.getUnformattedText();
							displayName = formatNick(displayName, context, target, mode, null);
							component = new ChatComponentText(displayName);
							if(mode != Mode.Emote) {
								EnumChatFormatting nameColor = IRCFormatting.getColorFormattingForPlayer(player);
								if(nameColor != null && nameColor != EnumChatFormatting.WHITE) {
									component.getChatStyle().setColor(nameColor);
								}
							}
						} else {
							component = new ChatComponentText(sender.getCommandSenderName());
						}
					} else if(token.equals("MESSAGE")) {
						BotSettings botSettings = ConfigHelper.getBotSettings(context);
						if(target == Target.Minecraft) {
							message = IRCFormatting.toMC(message, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
							message = filterAllowedCharacters(message);
						} else if(target == Target.IRC) {
							message = IRCFormatting.toIRC(message, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
						}
						component = createChatComponentForMessage(message);
					} else {
						validToken = false;
					}
					if(validToken) {
						if(sb.length() > 0) {
							IChatComponent newComponent;
							newComponent = new ChatComponentText(sb.toString());
							root.appendSibling(newComponent);
							if(nextColor != null) {
								newComponent.getChatStyle().setColor(nextColor);
							}
							sb = new StringBuilder();
						}
						root.appendSibling(component);
						if(nextColor != null) {
							component.getChatStyle().setColor(nextColor);
						}
						currentIdx += token.length() + 2;
						continue;
					}
				}
			} else if(c == '\u00a7') {
				nextColor = IRCFormatting.getColorFromMCColorCode(format.charAt(currentIdx + 1));
				currentIdx += 2;
				continue;
			}
			sb.append(c);
			currentIdx++;
		}
		if(sb.length() > 0) {
			IChatComponent newComponent;
			newComponent = new ChatComponentText(sb.toString());
			root.appendSibling(newComponent);
			if(nextColor != null) {
				newComponent.getChatStyle().setColor(nextColor);
			}
		}
		return root;
	}

	public static String formatMessage(String format, IRCConnection connection, IRCContext targetContext, IRCUser user, String message, Target target, Mode mode) {
		String result = formatChatComponent(format, connection, targetContext, user, message, target, mode).getUnformattedText();
		BotSettings botSettings = ConfigHelper.getBotSettings(targetContext);
		if(target == Target.IRC) {
			result = IRCFormatting.toIRC(result, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
		} else if(target == Target.Minecraft) {
			result = IRCFormatting.toMC(result, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
			result = filterAllowedCharacters(result);
		}
		return result;
	}

	public static IChatComponent formatChatComponent(String format, IRCConnection connection, IRCContext targetContext, IRCUser sender, String message, Target target, Mode mode) {
		IChatComponent root = new ChatComponentText("");
		EnumChatFormatting nextColor = null;
		StringBuilder sb = new StringBuilder();
		int currentIdx = 0;
		while(currentIdx < format.length()) {
			char c = format.charAt(currentIdx);
			if(c == '{') {
				int tokenEnd = format.indexOf('}', currentIdx);
				if(tokenEnd != -1) {
					boolean validToken = true;
					String token = format.substring(currentIdx + 1, tokenEnd);
					IChatComponent component = null;
					if(token.equals("SERVER")) {
						component = new ChatComponentText(connection.getIdentifier());
					} else if(token.equals("CHANNEL")) {
						component = new ChatComponentText(targetContext != null ? targetContext.getName() : "#");
					} else if(token.equals("USER")) {
						if(sender != null) {
							component = new ChatComponentText(sender.getIdentifier());
						} else {
							component = new ChatComponentText(connection.getIdentifier());
						}
					} else if(token.equals("NICK")) {
						if(sender != null) {
							String displayName = sender.getName();
							displayName = formatNick(displayName, targetContext, target, mode, sender);
							component = new ChatComponentText(displayName);
							if(mode != Mode.Emote) {
								EnumChatFormatting nameColor = IRCFormatting.getColorFormattingForUser(targetContext instanceof IRCChannel ? (IRCChannel) targetContext : null, sender);
								if(nameColor != null) {
									component.getChatStyle().setColor(nameColor);
								}
							}
						} else {
							component = new ChatComponentText(connection.getIdentifier());
						}
					} else if(token.equals("MESSAGE")) {
						BotSettings botSettings = ConfigHelper.getBotSettings(targetContext);
						if(target == Target.Minecraft) {
							message = IRCFormatting.toMC(message, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
							message = filterAllowedCharacters(message);
						} else if(target == Target.IRC) {
							message = IRCFormatting.toIRC(message, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
						}
						component = createChatComponentForMessage(message);
					} else {
						validToken = false;
					}
					if(validToken) {
						if(sb.length() > 0) {
							IChatComponent newComponent;
							newComponent = new ChatComponentText(sb.toString());
							root.appendSibling(newComponent);
							if(nextColor != null) {
								newComponent.getChatStyle().setColor(nextColor);
							}
							sb = new StringBuilder();
						}
						root.appendSibling(component);
						if(nextColor != null) {
							component.getChatStyle().setColor(nextColor);
						}
						currentIdx += token.length() + 2;
						continue;
					}
				}
			} else if(c == '\u00a7') {
				if(sb.length() > 0) {
					IChatComponent newComponent = new ChatComponentText(sb.toString());
					root.appendSibling(newComponent);
					if(nextColor != null) {
						newComponent.getChatStyle().setColor(nextColor);
					}
					sb = new StringBuilder();
				}
				nextColor = IRCFormatting.getColorFromMCColorCode(format.charAt(currentIdx + 1));
				currentIdx += 2;
				continue;
			}
			sb.append(c);
			currentIdx++;
		}
		if(sb.length() > 0) {
			IChatComponent newComponent;
			newComponent = new ChatComponentText(sb.toString());
			root.appendSibling(newComponent);
			if(nextColor != null) {
				newComponent.getChatStyle().setColor(nextColor);
			}
		}
		return root;
	}

	private static boolean isAllowedCharacter(char c) {
		return c >= 32 && c != 127;
	}
}
