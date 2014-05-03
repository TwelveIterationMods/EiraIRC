// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.server.MinecraftServer;

public class BotCommandCustom implements IBotCommand {

	private String name;
	private String command;
	private boolean allowArgs;
	private boolean runAsOp;
	private boolean broadcastResult;
	
	public BotCommandCustom(String name, String command, boolean allowArgs, boolean broadcastResult, boolean runAsOp) {
		this.name = name;
		this.command = command;
		this.allowArgs = allowArgs;
		this.broadcastResult = broadcastResult;
		this.runAsOp = runAsOp;
	}
	
	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}
	
	public boolean runAsOp() {
		return runAsOp;
	}

	@Override
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		String message = command;
		if(allowArgs) {
			message += " " + Utils.joinArgs(args, 0).trim();
		}
		bot.resetLog();
		bot.setOpEnabled(runAsOp);
		MinecraftServer.getServer().getCommandManager().executeCommand(bot, message);
		bot.setOpEnabled(false);
		if(broadcastResult) {
			channel.message("> " + bot.getLogContents());
		} else {
			user.notice("> " + bot.getLogContents());
		}
	}

}
