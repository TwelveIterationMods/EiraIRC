// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.command.CommandConfig;
import net.blay09.mods.eirairc.command.CommandConnect;
import net.blay09.mods.eirairc.command.CommandDisconnect;
import net.blay09.mods.eirairc.command.CommandJoin;
import net.blay09.mods.eirairc.command.CommandLeave;
import net.blay09.mods.eirairc.command.CommandList;
import net.blay09.mods.eirairc.command.CommandMessage;
import net.blay09.mods.eirairc.command.CommandNick;
import net.blay09.mods.eirairc.command.CommandQuote;
import net.blay09.mods.eirairc.command.CommandWho;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.command.extension.CommandAlias;
import net.blay09.mods.eirairc.command.extension.CommandColor;
import net.blay09.mods.eirairc.command.extension.CommandGhost;
import net.blay09.mods.eirairc.command.extension.CommandNickServ;
import net.blay09.mods.eirairc.command.extension.CommandTwitch;
import net.blay09.mods.eirairc.command.interop.InterOpCommandKick;
import net.blay09.mods.eirairc.command.interop.InterOpCommandMode;
import net.blay09.mods.eirairc.command.interop.InterOpCommandTopic;
import net.blay09.mods.eirairc.command.interop.InterOpCommandUserModeBase;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class IRCCommandHandler {

	private static final Map<String, SubCommand> commands = new HashMap<String, SubCommand>();
	
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
		
		if(GlobalConfig.interOp) {
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
	}
	
	public static void registerCommand(SubCommand command) {
		commands.put(command.getCommandName(), command);
		List<String> aliases = command.getCommandAliases();
		if(aliases != null) {
			for(String alias : aliases) {
				commands.put(alias, command);
			}
		}
	}
	
	public static void registerQuickCommands(CommandHandler commandHandler) {
		for(SubCommand command : commands.values()) {
			if(command.hasQuickCommand()) {
				commandHandler.registerCommand(command);
			}
		}
	}
	
	public static boolean isUsernameIndex(String[] args, int idx) {
		SubCommand cmd = commands.get(args[0]);
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
		SubCommand cmd = commands.get(args[0]);
		if(cmd != null) {
			String[] shiftedArgs = Utils.shiftArgs(args, 1);
			return cmd.addTabCompletionOptions(sender, shiftedArgs);
		}
		return null;
	}

	public static boolean processCommand(ICommandSender sender, String[] args, boolean serverSide) {
		SubCommand cmd = commands.get(args[0]);
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
		return cmd.processCommand(sender, Utils.getSuggestedTarget(), shiftedArgs, serverSide);
	}
	
	public static void sendUsageHelp(ICommandSender sender) {
		Utils.sendLocalizedMessage(sender, "irc.general.usage", Utils.getLocalizedMessage("irc.commands.irc"));
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.general");
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.irc");
		if(GlobalConfig.interOp) {
			Utils.sendLocalizedMessage(sender, "irc.cmdlist.interop");
		}
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.special");
	}

	public static boolean onChatCommand(EntityPlayer sender, String text, boolean serverSide) {
		if(!text.startsWith("!")) {
			return false;
		}
		String[] params = text.substring(1).split(" ");
		try {
			return processCommand(sender, params, serverSide);
		} catch (WrongUsageException e) {
			sender.addChatMessage(Utils.getLocalizedChatMessage("irc.general.usage", Utils.getLocalizedMessageNoPrefix(e.getMessage())));
			return true;
		}
	}

}
