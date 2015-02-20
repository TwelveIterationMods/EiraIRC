// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.interop;

import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.IRCTargetError;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class InterOpCommandTopic extends SubCommand {

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.interop.topic";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "topic";
	}
	
	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCContext targetChannel = IRCResolver.resolveTarget(args[0], (short) (IRCResolver.FLAG_CHANNEL + IRCResolver.FLAG_ONCHANNEL));
		if(targetChannel instanceof IRCTargetError) {
			Utils.sendLocalizedMessage(sender, targetChannel.getName(), args[0]);
			return true;
		}
		if(!ConfigHelper.getBotSettings(targetChannel).getBoolean(BotBooleanComponent.InterOp)) {
			Utils.sendLocalizedMessage(sender, "irc.interop.disabled");
			return true;
		}
		String topic = Utils.joinStrings(args, " ", 1).trim();
		if(topic.isEmpty()) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		targetChannel.getConnection().topic(targetChannel.getName(), topic);
		Utils.sendLocalizedMessage(sender, "irc.interop.topic", targetChannel.getName(), topic);
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public boolean hasQuickCommand() {
		return false;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
	}

}
