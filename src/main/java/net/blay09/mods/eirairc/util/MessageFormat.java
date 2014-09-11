package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

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
	private static final Pattern urlPattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

	public static String getMessageFormat(IRCBot bot, IRCContext context, boolean isEmote) {
		if(context instanceof IRCUser) {
			if(isEmote) {
				return ConfigHelper.getDisplayFormat(bot.getDisplayFormat(context)).ircPrivateEmote;
			} else {
				return ConfigHelper.getDisplayFormat(bot.getDisplayFormat(context)).ircPrivateMessage;
			}
		} else {
			if(isEmote) {
				return ConfigHelper.getDisplayFormat(bot.getDisplayFormat(context)).ircChannelEmote;
			} else {
				return ConfigHelper.getDisplayFormat(bot.getDisplayFormat(context)).ircChannelMessage;
			}
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

	public static String addPreSuffix(String name) {
		return GlobalConfig.nickPrefix + name + GlobalConfig.nickSuffix;
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

	private static String formatNick(String nick, Target target, Mode mode) {
		if(target == Target.IRC) {
			if (DisplayConfig.hidePlayerTags) {
				nick = filterPlayerTags(nick);
			}
			nick = addPreSuffix(nick);
		}
		return nick;
	}

	public static String formatMessage(String format, IRCContext context, ICommandSender sender, String message, Target target, Mode mode) {
		String result = formatChatComponent(format, context, sender, message, target, mode).getUnformattedText();
		if(target == Target.IRC) {
			result = IRCFormatting.toIRC(result, !DisplayConfig.enableIRCColors);
		} else if(target == Target.Minecraft) {
			result = IRCFormatting.toMC(result, !DisplayConfig.enableIRCColors);
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
							displayName = formatNick(displayName, target, mode);
							component = new ChatComponentText(displayName);
							if(mode != Mode.Emote) {
								component.getChatStyle().setColor(Utils.getColorFormattingForPlayer(player));
							}
						} else {
							component = new ChatComponentText(sender.getCommandSenderName());
						}
					} else if(token.equals("MESSAGE")) {
						if(target == Target.Minecraft) {
							message = IRCFormatting.toMC(message, !DisplayConfig.enableIRCColors);
							message = filterAllowedCharacters(message);
						} else if(target == Target.IRC) {
							message = IRCFormatting.toIRC(message, !DisplayConfig.enableIRCColors);
						}
						component = new ChatComponentText(message);
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
		if(target == Target.IRC) {
			result = IRCFormatting.toIRC(result, !DisplayConfig.enableIRCColors);
		} else if(target == Target.Minecraft) {
			result = IRCFormatting.toMC(result, !DisplayConfig.enableIRCColors);
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
							displayName = formatNick(displayName, target, mode);
							component = new ChatComponentText(displayName);
							if(mode != Mode.Emote) {
								component.getChatStyle().setColor(Utils.getColorFormattingForUser(channel, user));
							}
						} else {
							component = new ChatComponentText(connection.getIdentifier());
						}
					} else if(token.equals("MESSAGE")) {
						if(target == Target.Minecraft) {
							message = IRCFormatting.toMC(message, !DisplayConfig.enableIRCColors);
							message = filterAllowedCharacters(message);
						} else if(target == Target.IRC) {
							message = IRCFormatting.toIRC(message, !DisplayConfig.enableIRCColors);
						}
						component = new ChatComponentText(message);
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
