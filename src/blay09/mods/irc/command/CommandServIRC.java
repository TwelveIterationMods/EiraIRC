// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import blay09.mods.irc.config.Globals;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandServIRC implements ICommand {

	@Override
	public String getCommandName() {
		return "servirc";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.irc.usage";
	}
	
	public boolean isOP(ICommandSender sender) {
		if(sender instanceof EntityPlayer) {
			if(MinecraftServer.getServer().isSinglePlayer()) {
				return true;
			}
			return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).getOps().contains(((EntityPlayer)sender).username.toLowerCase().trim());
		}
		return true;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(MinecraftServer.getServer().isSinglePlayer()) {
			IRCCommandHandler.sendLocalizedMessage(sender, "irc.notMultiplayer");
			return;
		}
		if(args.length < 1) {
			 throw new WrongUsageException("commands.irc.usage", "servIrc");	
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
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}
}
