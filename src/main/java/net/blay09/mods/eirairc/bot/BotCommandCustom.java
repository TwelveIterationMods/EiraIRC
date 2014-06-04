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

	private String catName;
	private String name = "";
	private String command = "";
	private String description;
	private boolean allowArgs;
	private boolean runAsOp;
	private boolean requireAuth;
	private boolean broadcastResult;
	
	public BotCommandCustom() {
	}
	
	public BotCommandCustom(String catName, String name, String command, String description, boolean allowArgs, boolean broadcastResult, boolean runAsOp, boolean requireAuth) {
		this.catName = catName;
		this.name = name;
		this.command = command;
		this.description = description;
		this.allowArgs = allowArgs;
		this.broadcastResult = broadcastResult;
		this.runAsOp = runAsOp;
		this.requireAuth = requireAuth;
	}
	
	public String getCategoryName() {
		return catName;
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
		if(requireAuth && (!bot.getProfile(channel).isInterOpAuth(user.getAuthLogin()))) {
			user.notice(Utils.getLocalizedMessage("irc.bot.noPermission"));
			return;
		}
		String message = command;
		if(allowArgs) {
			message += " " + Utils.joinStrings(args, " ", 0).trim();
		}
		MinecraftServer.getServer().getCommandManager().executeCommand(new IRCUserCommandSender(channel, user, broadcastResult, runAsOp), message);
	}

	public boolean allowsArgs() {
		return allowArgs;
	}

	public boolean isRunAsOp() {
		return runAsOp;
	}
	
	public boolean requiresAuth() {
		return requireAuth;
	}
	
	public boolean isBroadcastResult() {
		return broadcastResult;
	}

	public String getMinecraftCommand() {
		return command;
	}

	public void setRequireAuth(boolean requireAuth) {
		this.requireAuth = requireAuth;
	}
	
	public void setAllowArgs(boolean allowArgs) {
		this.allowArgs = allowArgs;
	}
	
	public void setRunAsOp(boolean runAsOp) {
		this.runAsOp = runAsOp;
	}
	
	public void setBroadcastResult(boolean broadcastResult) {
		this.broadcastResult = broadcastResult;
	}

	public void setCommandName(String name) {
		this.name = name;
	}
	
	public void setMinecraftCommand(String command) {
		this.command = command;
	}
	
	public void setCommandDescription(String description) {
		this.description = description;
	}

	@Override
	public String getCommandDescription() {
		return description;
	}

}
