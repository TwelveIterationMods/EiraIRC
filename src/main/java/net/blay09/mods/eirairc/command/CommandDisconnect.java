// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class CommandDisconnect extends SubCommand {

	private static final String TARGET_ALL = "all";
	
	@Override
	public String getCommandName() {
		return "disconnect";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.disconnect";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		String target = null;
		if(args.length < 1) {
			if(context != null) {
				target = context.getConnection().getHost();
			} else {
				Utils.sendLocalizedMessage(sender, "irc.target.specifyServer");
				return true;
			}
		} else {
			target = args[0];
		}
		if(target.equals(TARGET_ALL)) {
			Utils.sendLocalizedMessage(sender, "irc.basic.disconnecting", "IRC");
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.disconnect(ConfigHelper.getQuitMessage(connection));
			}
			EiraIRC.instance.clearConnections();
		} else {
			IRCConnection connection = EiraIRC.instance.getConnection(target);
			if(connection == null) {
				Utils.sendLocalizedMessage(sender, "irc.general.notConnected", target);
				return true;
			}
			Utils.sendLocalizedMessage(sender, "irc.basic.disconnecting", target);
			connection.disconnect(ConfigHelper.getQuitMessage(connection));
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args, BlockPos pos) {
		list.add(TARGET_ALL);
		Utils.addConnectionsToList(list);
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
