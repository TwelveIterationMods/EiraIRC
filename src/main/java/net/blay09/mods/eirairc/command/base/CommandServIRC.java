// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command.base;

import net.blay09.mods.eirairc.util.ChatComponentBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.List;

public class CommandServIRC implements ICommand {

	@Override
	public int compareTo(ICommand o) {
		return getCommandName().compareTo((o.getCommandName()));
	}

	@Override
	public String getCommandName() {
		return "servirc";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:commands.servirc.usage";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("sirc");
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length == 0) {
			IRCCommandHandler.sendUsageHelp(sender);
			return;
		}
		if(MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
			ChatComponentBuilder.create().color('c').lang("eirairc:general.notMultiplayer").send(sender);
			return;
		}
		try {
			IRCCommandHandler.processCommand(sender, args, true);
		} catch (CommandException e) {
			ChatComponentBuilder ccb = new ChatComponentBuilder();
			ccb.color('c').lang("commands.generic.usage", ccb.push().lang(e.getMessage(), e.getErrorObjects()).pop()).send(sender);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return IRCCommandHandler.addTabCompletionOptions(sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] sender, int args) {
		return IRCCommandHandler.isUsernameIndex(sender, args);
	}

}
