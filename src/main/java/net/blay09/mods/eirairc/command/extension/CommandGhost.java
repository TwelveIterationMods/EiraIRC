// Copyright (c) 2015 Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.command.extension;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.base.ServiceConfig;
import net.blay09.mods.eirairc.config.base.ServiceSettings;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandGhost implements SubCommand {

	@Override
	public String getCommandName() {
		return "ghost";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.ghost";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		IRCConnection connection;
		if(args.length > 0) {
			connection = EiraIRCAPI.parseContext(null, args[0], null).getConnection();
			if(connection == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[0]);
				return true;
			}
		} else {
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.specifyServer");
				return true;
			}
			connection = context.getConnection();
		}
		ServiceSettings settings = ServiceConfig.getSettings(connection.getHost(), connection.getServerType());
		if(settings.hasGhostCommand()) {
			AuthManager.NickServData nickServData = AuthManager.getNickServData(connection.getIdentifier());
			if(nickServData != null) {
				connection.irc(settings.getGhostCommand(nickServData.username, nickServData.password));
			}
		} else {
			Utils.sendLocalizedMessage(sender, "irc.general.notSupported", "GHOST");
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
			Utils.addConnectionsToList(list);
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
