// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.base;

import java.util.ArrayList;
import java.util.List;

import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class CommandServIRC implements ICommand {

	@Override
	public int compareTo(Object o) {
		return getCommandName().compareTo(((ICommand) o).getCommandName());
	}

	@Override
	public String getCommandName() {
		return "servirc";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return Globals.MOD_ID + ":irc.commands.servirc";
	}

	@Override
	public List getCommandAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.add("sirc");
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length == 0) {
			IRCCommandHandler.sendUsageHelp(sender);
			return;
		}
		if(MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
			Utils.sendLocalizedMessage(sender, "irc.general.notMultiplayer");
			return;
		}
		try {
			IRCCommandHandler.processCommand(sender, args, false);
		} catch (WrongUsageException e) {
			ChatMessageComponent chatComponent = ChatMessageComponent.createFromTranslationWithSubstitutions("commands.generic.usage", Utils.getLocalizedMessageNoPrefix(e.getMessage(), e.getErrorOjbects()));
			chatComponent.setColor(EnumChatFormatting.RED);
			sender.sendChatToPlayer(chatComponent);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return IRCCommandHandler.addTabCompletionOptions(sender, args);
	}

	@Override
	public boolean isUsernameIndex(String[] sender, int args) {
		return IRCCommandHandler.isUsernameIndex(sender, args);
	}

}
