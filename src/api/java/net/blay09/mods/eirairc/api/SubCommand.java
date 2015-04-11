// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.List;

/**
 * An interface that can be used to register your own sub-command to the /irc command.
 */
public interface SubCommand {

	/**
	 * @return the name of this command
	 */
	String getCommandName();

	/**
	 * @return whether this command should be registered as it's own Minecraft command if registerShortCommands is enabled
	 */
	boolean hasQuickCommand();

	/**
	 * @param sender the sender that issued the command
	 * @param context the context the command was run within or null
	 * @param args array of arguments the command was run with
	 * @param serverSide true if this command was run via the server-side irc command
	 * @return true if the command was handled
	 */
	boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException;

	/**
	 * @param list the list to add the tab completion options to
	 * @param sender the sender that issued the command
	 * @param args array of arguments the command was run with
	 */
	void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args);

	/**
	 * @param sender the sender that issued the command
	 * @return true if the sender is allowed to use this command
	 */
	boolean canCommandSenderUseCommand(ICommandSender sender);

	/**
	 * @param args array of arguments the command was run with
	 * @param idx the index being checked
	 * @return true if args[idx] is assumed to be a username
	 */
	boolean isUsernameIndex(String[] args, int idx);

	/**
	 * @param sender the sender that issued the command
	 * @return a language key to describe the command usage
	 */
	String getCommandUsage(ICommandSender sender);

	/**
	 * @return an array of aliases for this command or null
	 */
	String[] getAliases();

}
