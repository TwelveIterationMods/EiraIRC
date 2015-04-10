package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.blay09.mods.eirairc.api.bot.IRCBot;


public class ReloadBotCommandsEvent extends Event {
	public final IRCBot bot;

	public ReloadBotCommandsEvent(IRCBot bot) {
		this.bot = bot;
	}
}
