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
	private boolean runAsOp;
	
	public BotCommandCustom(String name, String command, boolean runAsOp) {
		this.name = name;
		this.command = command;
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
		String message = Utils.joinArgs(args, 0).trim();
		bot.resetLog();
		MinecraftServer.getServer().getCommandManager().executeCommand(bot, message);
		user.notice("> " + bot.getLogContents());
	}

}
