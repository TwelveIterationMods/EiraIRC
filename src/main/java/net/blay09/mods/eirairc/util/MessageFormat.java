package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Blay09 on 01.09.2014.
 */
public class MessageFormat {

	public static enum Target {
		IRC,
		Minecraft
	}

	public static enum Mode {
		Message,
		Emote;
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
		ChatComponentText rootComponent = null;
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

	private static ChatComponentText appendSiblingToRoot(ChatComponentText root, IChatComponent sibling) {
		if(root == null) {
			root = new ChatComponentText("");
		}
		root.appendSibling(sibling);
		return root;
	}

	private static ChatComponentText appendTextToRoot(ChatComponentText root, String text) {
		if(root == null) {
			root = new ChatComponentText(text);
			return root;
		} else {
			root.appendText(text);
			return root;
		}
	}

	public static String filterLinks(String message) {
		String[] s = message.split(" ");
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.length; i++) {
			Matcher matcher = urlPattern.matcher(s[i]);
			sb.append(((i > 0) ? " " : "") + matcher.replaceAll(Utils.getLocalizedMessage("irc.general.linkRemoved")));
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
			if (SharedGlobalConfig.hidePlayerTags) {
				nick = filterPlayerTags(nick);
			}
			nick = String.format(ConfigHelper.getBotSettings(context).getString(BotStringComponent.NickFormat), nick);
		} else if(target == Target.Minecraft && context instanceof IRCChannel) {
			GeneralSettings settings = ConfigHelper.getGeneralSettings(context);
			if(settings.getBoolean(GeneralBooleanComponent.ShowNameFlags)) {
				nick = ircUser.getChannelModePrefix((IRCChannel) context) + nick;
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
							component = player.func_145748_c_().createCopy();
							String displayName = component.getUnformattedText();
							displayName = formatNick(displayName, context, target, mode, null);
							component = new ChatComponentText(displayName);
							if(mode != Mode.Emote) {
								EnumChatFormatting nameColor = Utils.getColorFormattingForPlayer(player);
								if(nameColor != null) {
									component.getChatStyle().setColor(Utils.getColorFormattingForPlayer(player));
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
							root.appendSibling(new ChatComponentText(sb.toString()));
							sb = new StringBuilder();
						}
						root.appendSibling(component);
						currentIdx += token.length() + 2;
						continue;
					}
				}
			}
			sb.append(c);
			currentIdx++;
		}
		if(sb.length() > 0) {
			root.appendSibling(new ChatComponentText(sb.toString()));
		}
		return root;
	}

	public static String formatMessage(String format, IRCConnection connection, IRCChannel channel, IRCUser user, String message, Target target, Mode mode) {
		String result = formatChatComponent(format, connection, channel, user, message, target, mode).getUnformattedText();
		BotSettings botSettings = ConfigHelper.getBotSettings(channel);
		if(target == Target.IRC) {
			result = IRCFormatting.toIRC(result, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
		} else if(target == Target.Minecraft) {
			result = IRCFormatting.toMC(result, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
			result = filterAllowedCharacters(result);
		}
		return result;
	}

	public static IChatComponent formatChatComponent(String format, IRCConnection connection, IRCChannel channel, IRCUser user, String message, Target target, Mode mode) {
		IChatComponent root = new ChatComponentText("");
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
						component = new ChatComponentText(channel.getName());
					} else if(token.equals("USER")) {
						if(user != null) {
							component = new ChatComponentText(user.getIdentifier());
						} else {
							component = new ChatComponentText(connection.getIdentifier());
						}
					} else if(token.equals("NICK")) {
						if(user != null) {
							String displayName = user.getName();
							displayName = formatNick(displayName, channel, target, mode, user);
							component = new ChatComponentText(displayName);
							if(mode != Mode.Emote) {
								EnumChatFormatting nameColor = Utils.getColorFormattingForUser(channel, user);
								if(nameColor != null) {
									component.getChatStyle().setColor(nameColor);
								}
							}
						} else {
							component = new ChatComponentText(connection.getIdentifier());
						}
					} else if(token.equals("MESSAGE")) {
						BotSettings botSettings = ConfigHelper.getBotSettings(channel);
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
							root.appendSibling(new ChatComponentText(sb.toString()));
							sb = new StringBuilder();
						}
						root.appendSibling(component);
						currentIdx += token.length() + 2;
						continue;
					}
				}
			}
			sb.append(c);
			currentIdx++;
		}
		if(sb.length() > 0) {
			root.appendSibling(new ChatComponentText(sb.toString()));
		}
		return root;
	}

	private static boolean isAllowedCharacter(char c) {
		return c >= 32 && c != 127;
	}
}
