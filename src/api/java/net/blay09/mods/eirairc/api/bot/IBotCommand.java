// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;

public interface IBotCommand {

	/**
	 * @return the name of this bot command without any command prefixes
	 */
	public String getCommandName();

	/**
	 * @return the description of this bot command that will be shown in HELP
	 */
	public String getCommandDescription();

	/**
	 * @return true if this command can be run directly in a channel using a command prefix
	 */
	public boolean isChannelCommand();

	 /**
	 * @param bot the bot this command is run on
	 * @param channel the channel this command is run within or null
	 * @param user the user that ran this command
	 * @param args the arguments this command was ran with
	 * @param commandSettings the override settings if this was forwarded from a custom command or this
	 */
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args, IBotCommand commandSettings);

	/**
	 * @return true if this command requires the sender to be authenticated with EiraIRC
	 */
	public boolean requiresAuth();

	/**
	 * @return true if this command should broadcast its result to the whole channel, when run within a channel
	 */
	public boolean broadcastsResult();

	/**
	 * @return true if this command allows arguments
	 */
	public boolean allowArgs();

}
