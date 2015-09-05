// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.ChatComponentBuilder;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.blay09.mods.eirairc.wrapper.CommandSender;
import net.blay09.mods.eirairc.wrapper.SubCommandWrapper;
import net.minecraft.command.CommandException;

import java.util.List;

public class CommandTwitch implements SubCommand {

	@Override
	public String getCommandName() {
		return "twitch";
	}

	@Override
	public String getCommandUsage(CommandSender sender) {
		return "eirairc:commands.twitch.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(CommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(EiraIRCAPI.isConnectedTo(Globals.TWITCH_SERVER)) {
			ChatComponentBuilder.create().color('c').lang("eirairc:error.alreadyConnected", "Twitch").send(sender);
			return true;
		}
		if(args.length == 0) {
			if(ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER)) {
				Utils.sendLocalizedMessage(sender, "commands.connect", "Twitch");
				ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
				ConnectionManager.connectTo(serverConfig);
				return true;
			} else {
				if(serverSide) {
					SubCommandWrapper.throwWrongUsageException(this, sender);
					return true;
				} else {
					ChatComponentBuilder.create().color('c').lang("eirairc:general.serverOnlyCommand").send(sender);
					return true;
				}
			}
		} else {
			if(args.length < 2) {
				SubCommandWrapper.throwWrongUsageException(this, sender);
			}
			ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
			serverConfig.setNick(args[0]);
			AuthManager.putServerPassword(serverConfig.getIdentifier(), args[1]);
			serverConfig.getOrCreateChannelConfig("#" + serverConfig.getNick());
			serverConfig.getGeneralSettings().readOnly.set(false);
			serverConfig.getBotSettings().messageFormat.set("Twitch");
			ConfigurationHandler.addServerConfig(serverConfig);
			ConfigurationHandler.save();
			Utils.sendLocalizedMessage(sender, "commands.connect", "Twitch");
			ConnectionManager.connectTo(serverConfig);
			return true;
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(CommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, CommandSender sender, String[] args) {
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
