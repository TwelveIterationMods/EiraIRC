package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.minecraftforge.fml.common.eventhandler.Event;


public class ReloadBotCommandsEvent extends Event {
	public final IRCBot bot;

	public ReloadBotCommandsEvent(IRCBot bot) {
		this.bot = bot;
	}
}
