// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.config.settings.BotStringListComponent;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;

public class IRCBotImpl implements IRCBot {

	private final IRCConnectionImpl connection;
	private final Map<String, IBotCommand> commands = new HashMap<String, IBotCommand>();

	public IRCBotImpl(IRCConnectionImpl connection) {
		this.connection = connection;

		if(isServerSide()) {
			registerCommand(new BotCommandAlias());
			registerCommand(new BotCommandHelp());
			registerCommand(new BotCommandHelp());
			registerCommand(new BotCommandMessage());
			registerCommand(new BotCommandWho("who"));
			registerCommand(new BotCommandWho("players"));
			registerCommand(new BotCommandOp());
		}
	}

	@Override
	public void registerCommand(IBotCommand command) {
		commands.put(command.getCommandName().toLowerCase(), command);
	}

	@Override
	public Collection<IBotCommand> getCommands() {
		return commands.values();
	}

	@Override
	public IRCConnection getConnection() {
		return connection;
	}

	@Override
	public boolean processCommand(IRCChannel channel, IRCUser sender, String message) {
		String[] args = message.split(" ");
		IBotCommand botCommand = commands.get(args[0].toLowerCase());
		if(ConfigHelper.getBotSettings(channel).containsString(BotStringListComponent.DisabledNativeCommands, args[0].toLowerCase())) {
			return false;
		}
		if(channel != null && !botCommand.isChannelCommand()) {
			return false;
		}
		String[] shiftedArgs = Utils.shiftArgs(args, 1);
		if(botCommand.requiresAuth()) {
			((IRCUserImpl) sender).queueAuthCommand(this, channel, botCommand, shiftedArgs);
		} else {
			botCommand.processCommand(this, channel, sender, shiftedArgs);
		}
		return true;
	}

	@Override
	public boolean isServerSide() {
		return Utils.isServerSide();
	}

}
