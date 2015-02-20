// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class CommandJoin extends SubCommand {

	@Override
	public String getCommandName() {
		return "join";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.join";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCConnection connection;
		if(IRCResolver.hasServerPrefix(args[0])) {
			connection = IRCResolver.resolveConnection(args[0], IRCResolver.FLAGS_NONE);
			if(connection == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound");
				return true;
			}
		} else {
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.specifyServer");
				return true;
			}
			connection = context.getConnection();
		}
		ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
		String channelName = IRCResolver.stripPath(args[0]);
		ChannelConfig channelConfig = serverConfig.getOrCreateChannelConfig(channelName);
		if(args.length >= 2) {
			channelConfig.setPassword(args[1]);
		}
		Utils.sendLocalizedMessage(sender, "irc.basic.joiningChannel", channelConfig.getName(), connection.getHost());
		connection.join(channelConfig.getName(), channelConfig.getPassword());
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		if(args.length == 0) {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					list.add(channelConfig.getName());
				}
			}
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
