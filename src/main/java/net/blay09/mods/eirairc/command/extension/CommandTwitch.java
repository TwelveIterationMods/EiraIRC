// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command.extension;

import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.settings.GeneralBooleanComponent;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class CommandTwitch implements SubCommand {

	@Override
	public String getCommandName() {
		return "twitch";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:commands.twitch.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(EiraIRCAPI.isConnectedTo(Globals.TWITCH_SERVER)) {
			Utils.sendLocalizedMessage(sender, "general.alreadyConnected", "Twitch");
			return true;
		}
		if(args.length == 0) {
			if(ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER)) {
				Utils.sendLocalizedMessage(sender, "general.connecting", "Twitch");
				ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
				ConnectionManager.connectTo(serverConfig);
				return true;
			} else {
				if(serverSide) {
					throw new WrongUsageException(getCommandUsage(sender));
				} else {
					Utils.sendLocalizedMessage(sender, "general.serverOnlyCommand");
					return true;
				}
			}
		} else {
			if(args.length < 2) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.twitch.usage");
			}
			ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
			serverConfig.setNick(args[0]);
			AuthManager.putServerPassword(serverConfig.getIdentifier(), args[1]);
			serverConfig.getOrCreateChannelConfig("#" + serverConfig.getNick());
			serverConfig.getGeneralSettings().setBoolean(GeneralBooleanComponent.ReadOnly, false);
			serverConfig.getBotSettings().setString(BotStringComponent.MessageFormat, "Twitch");
			ConfigurationHandler.addServerConfig(serverConfig);
			ConfigurationHandler.save();
			Utils.sendLocalizedMessage(sender, "general.connecting", "Twitch");
			ConnectionManager.connectTo(serverConfig);
			return true;
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
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
