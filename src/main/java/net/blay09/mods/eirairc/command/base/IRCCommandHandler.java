// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command.base;

import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.command.*;
import net.blay09.mods.eirairc.command.extension.*;
import net.blay09.mods.eirairc.command.interop.InterOpCommandKick;
import net.blay09.mods.eirairc.command.interop.InterOpCommandMode;
import net.blay09.mods.eirairc.command.interop.InterOpCommandTopic;
import net.blay09.mods.eirairc.command.interop.InterOpCommandUserModeBase;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.ChatComponentBuilder;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRCCommandHandler {

	private static final Map<String, SubCommandWrapper> commands = new HashMap<String, SubCommandWrapper>();
	
	public static void registerCommands() {
		registerCommand(new CommandConnect());
		registerCommand(new CommandDisconnect());
		registerCommand(new CommandJoin());
		registerCommand(new CommandLeave());
		registerCommand(new CommandConfig());
		registerCommand(new CommandList());
		registerCommand(new CommandMessage());
		registerCommand(new CommandCTCP());
		registerCommand(new CommandNick());
		registerCommand(new CommandNickServ());
		registerCommand(new CommandQuote());
		registerCommand(new CommandTwitch());
		registerCommand(new CommandWho());
		registerCommand(new CommandColor());
		registerCommand(new CommandGhost());
		registerCommand(new CommandIgnore());
		registerCommand(new CommandUnignore());

		registerCommand(new InterOpCommandKick());
		registerCommand(new InterOpCommandMode());
		registerCommand(new InterOpCommandTopic());
		registerCommand(new InterOpCommandUserModeBase("op", "+o", false));
		registerCommand(new InterOpCommandUserModeBase("deop", "-o", false));
		registerCommand(new InterOpCommandUserModeBase("voice", "+v", false));
		registerCommand(new InterOpCommandUserModeBase("devoice", "-v", false));
		registerCommand(new InterOpCommandUserModeBase("ban", "+b", true));
		registerCommand(new InterOpCommandUserModeBase("unban", "-b", true));
	}
	
	public static void registerCommand(SubCommand command) {
		SubCommandWrapper wrapper = new SubCommandWrapper(command);
		commands.put(command.getCommandName(), wrapper);
		String[] aliases = command.getAliases();
		if(aliases != null) {
			for(String alias : aliases) {
				commands.put(alias, wrapper);
			}
		}
	}
	
	public static void registerQuickCommands(CommandHandler commandHandler) {
		for(SubCommandWrapper wrapper : commands.values()) {
			if(wrapper.command.hasQuickCommand()) {
				commandHandler.registerCommand(wrapper);
			}
		}
	}
	
	public static boolean isUsernameIndex(String[] args, int idx) {
		SubCommandWrapper cmd = commands.get(args[0]);
		if(cmd != null) {
			String[] shiftedArgs = ArrayUtils.subarray(args, 1, args.length);
			return cmd.isUsernameIndex(shiftedArgs, idx - 1);
		}
		return false;
	}

	public static List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if(args.length == 1) {
			List<String> list = new ArrayList<String>();
			if(args[0].isEmpty()) {
				list.addAll(commands.keySet());
			} else {
				for(String key : commands.keySet()) {
					if(key.startsWith(args[0])) {
						list.add(key);
					}
				}
			}
			return list;
		}
		SubCommandWrapper cmd = commands.get(args[0]);
		if(cmd != null) {
			String[] shiftedArgs = ArrayUtils.subarray(args, 1, args.length);
			return cmd.addTabCompletionOptions(sender, shiftedArgs);
		}
		return null;
	}

	public static boolean processCommand(ICommandSender sender, String[] args, boolean serverSide) {
		SubCommandWrapper cmd = commands.get(args[0]);
		if(cmd == null) {
			sendUsageHelp(sender);
			return false;
		}
		if(!cmd.canCommandSenderUseCommand(sender)) {
			ChatComponentBuilder.create().color('c').lang("commands.generic.permission").send(sender);
            return true;
		}
		String[] shiftedArgs = ArrayUtils.subarray(args, 1, args.length);
		return cmd.command.processCommand(sender, Utils.getSuggestedTarget(), shiftedArgs, serverSide);
	}
	
	public static void sendUsageHelp(ICommandSender sender) {
		ChatComponentBuilder ccb = new ChatComponentBuilder(2);
		ccb.color('c').lang("commands.generic.usage", ccb.push().lang("eirairc:commands.irc.usage").pop()).send(sender);
		ccb.color('e').lang("eirairc:commands.irc.list.general").color('f').text(" config, help, list, ignore, unignore").send(sender);
		ccb.color('e').lang("eirairc:commands.irc.list.irc").color('f').text(" connect, disconnect, join, leave, nick, msg, who").send(sender);
		ccb.color('e').lang("eirairc:commands.irc.list.interop").color('f').text(" op, deop, voice, devoice, kick, ban, unban, umode, mode, topic").send(sender);
		ccb.color('e').lang("eirairc:commands.irc.list.special").color('f').text(" twitch, color, ctcp, ghost, nickserv").send(sender);
	}

	public static boolean onChatCommand(EntityPlayer sender, String text, boolean serverSide) {
		if(text.equals(SharedGlobalConfig.ircCommandPrefix.get() + "who") || text.startsWith(SharedGlobalConfig.ircCommandPrefix.get() + "who ")) {
			String[] params = text.substring(1).split(" ");
			try {
				return processCommand(sender, params, serverSide);
			} catch (WrongUsageException e) {
				ChatComponentBuilder ccb = new ChatComponentBuilder();
				ccb.color('c').lang("commands.generic.usage", ccb.push().lang(e.getMessage(), e.getErrorOjbects()).pop()).send(sender);
				return true;
			}
		}
		return false;
	}

}
