package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCTarget;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandList extends SubCommand {

	@Override
	public String getCommandName() {
		return "list";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.list";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCTarget context, String[] args, boolean serverSide) {
		if(EiraIRC.instance.getConnectionCount() == 0) {
			Utils.sendLocalizedMessage(sender, "irc.general.notConnected", "IRC");
			return true;
		}
		Utils.sendLocalizedMessage(sender, "irc.list.activeConnections");
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			String channels = "";
			for(IRCChannel channel : connection.getChannels()) {
				if(channels.length() > 0) {
					channels += ", ";
				}
				channels += channel.getName();
			}
			Utils.sendUnlocalizedMessage(sender, " * " + connection.getHost() + " (" + channels + ")");
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
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
