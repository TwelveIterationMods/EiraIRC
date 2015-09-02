// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command.base;

import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.command.*;
import net.blay09.mods.eirairc.command.extension.*;
import net.blay09.mods.eirairc.command.interop.InterOpCommandKick;
import net.blay09.mods.eirairc.command.interop.InterOpCommandMode;
import net.blay09.mods.eirairc.command.interop.InterOpCommandTopic;
import net.blay09.mods.eirairc.command.interop.InterOpCommandUserModeBase;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
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

	public static List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
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
			return cmd.addTabCompletionOptions(sender, shiftedArgs, pos);
		}
		return null;
	}

	public static boolean processCommand(ICommandSender sender, String[] args, boolean serverSide) throws CommandException {
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
		String[] shiftedArgs = ArrayUtils.subarray(args, 1, args.length);
		return cmd.command.processCommand(sender, Utils.getSuggestedTarget(), shiftedArgs, serverSide);
	}
	
	public static void sendUsageHelp(ICommandSender sender) {
		Utils.sendLocalizedMessage(sender, "general.usage", I19n.format("eirairc:commands.irc.usage"));
		Utils.sendLocalizedMessage(sender, "commands.irc.list.general");
		Utils.sendLocalizedMessage(sender, "commands.irc.list.irc");
		Utils.sendLocalizedMessage(sender, "commands.irc.list.interop");
		Utils.sendLocalizedMessage(sender, "commands.irc.list.special");
	}

	public static boolean onChatCommand(EntityPlayer sender, String text, boolean serverSide) {
		if(text.equals(SharedGlobalConfig.ircCommandPrefix + "who") || text.startsWith(SharedGlobalConfig.ircCommandPrefix + "who ")) {
			String[] params = text.substring(1).split(" ");
			try {
				return processCommand(sender, params, serverSide);
			} catch (WrongUsageException e) {
				Utils.sendLocalizedMessage(sender, "eirairc:general.usage", I19n.format(e.getMessage()));
				return true;
			} catch (CommandException e) {
				sender.addChatMessage(new ChatComponentText(e.getMessage()));
				return true;
			}
		}
		return false;
	}

}
