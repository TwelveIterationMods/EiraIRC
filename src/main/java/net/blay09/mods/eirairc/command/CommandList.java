// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandList implements SubCommand {

	@Override
	public String getCommandName() {
		return "list";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.list";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(EiraIRC.instance.getConnectionManager().getConnectionCount() == 0) {
			Utils.sendLocalizedMessage(sender, "irc.general.notConnected", "IRC");
			return true;
		}
		Utils.sendLocalizedMessage(sender, "irc.list.activeConnections");
		for(IRCConnection connection : EiraIRC.instance.getConnectionManager().getConnections()) {
			StringBuilder sb = new StringBuilder();
			for(IRCChannel channel : connection.getChannels()) {
				if(sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(channel.getName());
			}
			sender.addChatMessage(new ChatComponentText(" * " + connection.getHost() + " (" + sb.toString() + ")"));
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
		return false;
	}

}
