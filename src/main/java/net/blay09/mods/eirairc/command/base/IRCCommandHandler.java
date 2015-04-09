// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.base;

import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.command.*;
import net.blay09.mods.eirairc.command.extension.*;
import net.blay09.mods.eirairc.command.interop.InterOpCommandKick;
import net.blay09.mods.eirairc.command.interop.InterOpCommandMode;
import net.blay09.mods.eirairc.command.interop.InterOpCommandTopic;
import net.blay09.mods.eirairc.command.interop.InterOpCommandUserModeBase;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

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
		registerCommand(new CommandNick());
		registerCommand(new CommandNickServ());
		registerCommand(new CommandQuote());
		registerCommand(new CommandTwitch());
		registerCommand(new CommandWho());
		registerCommand(new CommandColor());
		registerCommand(new CommandGhost());
		registerCommand(new CommandAlias());
		
		registerCommand(new InterOpCommandKick());
		registerCommand(new InterOpCommandMode());
		registerCommand(new InterOpCommandTopic());
		registerCommand(new InterOpCommandUserModeBase("op", "+o"));
		registerCommand(new InterOpCommandUserModeBase("deop", "-o"));
		registerCommand(new InterOpCommandUserModeBase("voice", "+v"));
		registerCommand(new InterOpCommandUserModeBase("devoice", "-v"));
		registerCommand(new InterOpCommandUserModeBase("ban", "+b"));
		registerCommand(new InterOpCommandUserModeBase("unban", "-b"));
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
			String[] shiftedArgs = Utils.shiftArgs(args, 1);
			return cmd.isUsernameIndex(shiftedArgs, idx - 1);
		}
		return false;
	}

	public static List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if(args.length == 0) {
			List<String> list = new ArrayList<String>();
			list.addAll(commands.keySet());
			return list;
		}
		SubCommandWrapper cmd = commands.get(args[0]);
		if(cmd != null) {
			String[] shiftedArgs = Utils.shiftArgs(args, 1);
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
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("commands.generic.permission");
			chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatComponent);
            return true;
		}
		String[] shiftedArgs = Utils.shiftArgs(args, 1);
		return cmd.command.processCommand(sender, Utils.getSuggestedTarget(), shiftedArgs, serverSide);
	}
	
	public static void sendUsageHelp(ICommandSender sender) {
		Utils.sendLocalizedMessage(sender, "irc.general.usage", Utils.getLocalizedMessage("irc.commands.irc"));
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.general");
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.irc");
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.interop");
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.special");
	}

	public static boolean onChatCommand(EntityPlayer sender, String text, boolean serverSide) {
		if(text.startsWith("!who") || text.startsWith("!players")) {
			String[] params = text.substring(1).split(" ");
			try {
				return processCommand(sender, params, serverSide);
			} catch (WrongUsageException e) {
				sender.addChatMessage(Utils.getLocalizedChatMessage("irc.general.usage", Utils.getLocalizedMessageNoPrefix(e.getMessage())));
				return true;
			}
		}
		return false;
	}

}
