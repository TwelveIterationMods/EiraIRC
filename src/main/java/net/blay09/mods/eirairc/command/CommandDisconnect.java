// Copyright (c) 2015 Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandDisconnect implements SubCommand {

	private static final String TARGET_ALL = "all";
	
	@Override
	public String getCommandName() {
		return "disconnect";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:commands.disconnect.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		String target;
		if(args.length < 1) {
			if(context != null) {
				target = context.getConnection().getHost();
			} else {
				Utils.sendLocalizedMessage(sender, "error.specifyServer");
				return true;
			}
		} else {
			target = args[0];
		}
		if(target.equals(TARGET_ALL)) {
			Utils.sendLocalizedMessage(sender, "general.disconnecting", "IRC");
			for(IRCConnection connection : EiraIRC.instance.getConnectionManager().getConnections()) {
				connection.disconnect(ConfigHelper.getQuitMessage(connection));
			}
			EiraIRC.instance.getConnectionManager().clearConnections();
		} else {
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(target);
			if(connection == null) {
				Utils.sendLocalizedMessage(sender, "general.notConnected", target);
				return true;
			}
			Utils.sendLocalizedMessage(sender, "general.disconnecting", target);
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
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		list.add(TARGET_ALL);
		Utils.addConnectionsToList(list);
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
