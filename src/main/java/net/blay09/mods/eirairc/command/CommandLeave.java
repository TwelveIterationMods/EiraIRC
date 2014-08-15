// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandLeave extends SubCommand {

	private static final String TARGET_ALL = "all";

	@Override
	public String getCommandName() {
		return "leave";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.leave";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "part" };
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(context == null && args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCConnection connection = null;
		if(args.length > 0) {
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
		String channelName = IRCResolver.stripPath(args[0]);
		if(channelName.equals(TARGET_ALL)) {
			for(IRCChannel channel : connection.getChannels()) {
				connection.part(channel.getName());
			}
			Utils.sendLocalizedMessage(sender, "irc.basic.leavingChannel", "<all>", connection.getHost());
		} else {
			Utils.sendLocalizedMessage(sender, "irc.basic.leavingChannel", channelName, connection.getHost());
			ConfigHelper.getServerConfig(connection).getChannelConfig(channelName).setAutoJoin(false);
			connection.part(channelName);
		}
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
