// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandServIRC implements ICommand {

	@Override
	public String getCommandName() {
		return "servirc";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "irc.commands.irc";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
			Utils.sendLocalizedMessage(sender, "irc.general.notMultiplayer");
			return;
		}
		if(args.length < 1) {
			IRCCommandHandler.sendIRCUsage(sender);
			return;	
		}
		IRCCommandHandler.processCommand(sender, args, true);
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return IRCCommandHandler.addTabCompletionOptions(getCommandName(), sender, args);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return IRCCommandHandler.isUsernameIndex(args, i);
	}
}
