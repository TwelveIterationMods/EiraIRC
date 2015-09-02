// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.event.ReloadBotCommandsEvent;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.settings.BotStringListComponent;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IRCBotImpl implements IRCBot {

	private final IRCConnectionImpl connection;
	private final Map<String, IBotCommand> commands = new HashMap<String, IBotCommand>();

	public IRCBotImpl(IRCConnectionImpl connection) {
		this.connection = connection;

		reloadCommands();
	}

	public void reloadCommands() {
		commands.clear();
		if(isServerSide()) {
			registerCommand(new BotCommandHelp());
			registerCommand(new BotCommandMessage());
			registerCommand(new BotCommandWho());
			registerCommand(new BotCommandOp());
			for(IBotCommand command : ConfigurationHandler.getCustomCommands()) {
				registerCommand(command);
			}
		}
		MinecraftForge.EVENT_BUS.post(new ReloadBotCommandsEvent(this));
	}

	@Override
	public void registerCommand(IBotCommand command) {
		commands.put(command.getCommandName().toLowerCase(), command);
	}

	public Collection<IBotCommand> getCommands() {
		return commands.values();
	}

	@Override
	public IRCConnection getConnection() {
		return connection;
	}

	public boolean processCommand(IRCChannel channel, IRCUser sender, String message) {
		String[] args = message.split(" ");
		if(ConfigHelper.getBotSettings(channel).containsString(BotStringListComponent.DisabledNativeCommands, args[0].toLowerCase())) {
			return false;
		}
		IBotCommand botCommand = commands.get(args[0].toLowerCase());
		if(botCommand == null || (channel != null && !botCommand.isChannelCommand())) {
			return false;
		}
		String[] shiftedArgs = ArrayUtils.subarray(args, 1, args.length);
		if(botCommand.requiresAuth()) {
			((IRCUserImpl) sender).queueAuthCommand(this, channel, botCommand, shiftedArgs);
		} else {
			botCommand.processCommand(this, channel, sender, shiftedArgs, botCommand);
		}
		return true;
	}

	@Override
	public boolean isServerSide() {
		return Utils.isServerSide();
	}

}
