// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandConfig extends SubCommand {

	public static final String TARGET_GLOBAL = "global";
	
	@Override
	public String getCommandName() {
		return "config";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.config";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "cfg" };
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String target = args[0];
		if(target.equals("reload")) {
			Utils.sendLocalizedMessage(sender, "irc.config.reload");
			EiraIRC.instance.getConnectionManager().stopIRC();
			ConfigurationHandler.reload();
			EiraIRC.instance.getConnectionManager().startIRC();
			return true;
		}
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String config = args[1];
		if(args.length > 2) {
			ConfigurationHandler.handleConfigCommand(sender, target, config, args[2]);
		} else {
			ConfigurationHandler.handleConfigCommand(sender, target, config);
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		if(args.length == 1) {
			list.add("reload");
			list.add(TARGET_GLOBAL);
			Utils.addConnectionsToList(list);
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				list.add(serverConfig.getAddress());
				for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					list.add(channelConfig.getName());
				}
			}
		} else if(args.length == 2) {
			if(args[0].equals("reload")) {
				return;
			}
			if(args[0].equals(TARGET_GLOBAL)) {
				ConfigurationHandler.addOptionsToList(list);
			} else if(args[0].contains("#")) {
				ChannelConfig.addOptionsToList(list);
			} else {
				ServerConfig.addOptionstoList(list);
			}
		} else if(args.length == 3) {
			if(args[0].equals("reload")) {
				return;
			}
			if(args[0].equals(TARGET_GLOBAL)) {
				ConfigurationHandler.addValuesToList(list, args[1]);
			} else if(args[0].contains("#")) {
				ChannelConfig.addValuesToList(list, args[1]);
			} else {
				ServerConfig.addValuesToList(list, args[1]);
			}
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return false;
	}

}
