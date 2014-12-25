// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCChannelImpl;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;

public class CommandWho extends SubCommand {

	@Override
	public String getCommandName() {
		return "who";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.who";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		IRCConnection connection = null;
		if(args.length > 0) {
			connection = IRCResolver.resolveConnection(args[0], IRCResolver.FLAGS_NONE);
			if(connection == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[0]);
				return true;
			}
		} else {
			if(context != null) {
				connection = context.getConnection();
			}
		}
		if(args.length > 0) {
			String channelName = IRCResolver.stripPath(args[0]);
			context = connection.getChannel(channelName);
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.channelNotFound", args[0]);
				return true;
			}
		}
		if(context instanceof IRCChannelImpl) {
			Utils.sendUserList(sender, connection, (IRCChannelImpl) context);
		} else if(connection != null) {
			for(IRCChannel channel : connection.getChannels()) {
				Utils.sendUserList(sender, connection, channel);
			}
		} else {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				IRCConnection con = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
				if(con != null) {
					for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
						IRCChannel channel = con.getChannel(channelConfig.getName());
						if (channel != null) {
							Utils.sendUserList(sender, con, channel);
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
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
