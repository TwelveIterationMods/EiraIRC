// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CommandConfig implements SubCommand {

	public static final String TARGET_GLOBAL = "global";
	
	@Override
	public String getCommandName() {
		return "config";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:commands.config.usage";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "cfg" };
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String target = args[0];
		if(target.equals("reload")) {
			Utils.sendLocalizedMessage(sender, "commands.config.reload");
			EiraIRC.instance.getConnectionManager().stopIRC();
			ConfigurationHandler.reloadAll();
			EiraIRC.instance.getConnectionManager().startIRC();
			return true;
		}
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String config = args[1];
		if(args.length > 2) {
			ConfigurationHandler.handleConfigCommand(sender, target, config, StringUtils.join(args, " ", 2));
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
				ConfigurationHandler.addOptionsToList(list, args[1], true);
			} else if(IRCResolver.isChannel(args[0])) {
				ChannelConfig.addOptionsToList(list, args[1], true);
			} else {
				ServerConfig.addOptionsToList(list, args[1], true);
			}
		} else if(args.length == 3) {
			if(args[0].equals("reload")) {
				return;
			}
			if(args[0].equals(TARGET_GLOBAL)) {
				ConfigurationHandler.addOptionsToList(list, args[1], false);
			} else if(IRCResolver.isChannel(args[0])) {
				ChannelConfig.addOptionsToList(list, args[1], false);
			} else {
				ServerConfig.addOptionsToList(list, args[1], false);
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
