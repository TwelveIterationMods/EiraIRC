// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;

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
	public boolean processCommand(ICommandSender sender, IIRCContext context, String[] args, boolean serverSide) {
		if(EiraIRC.instance.getConnectionCount() == 0) {
			Utils.sendLocalizedMessage(sender, "irc.general.notConnected", "IRC");
			return true;
		}
		Utils.sendLocalizedMessage(sender, "irc.list.activeConnections");
		for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
			String channels = "";
			for(IIRCChannel channel : connection.getChannels()) {
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
