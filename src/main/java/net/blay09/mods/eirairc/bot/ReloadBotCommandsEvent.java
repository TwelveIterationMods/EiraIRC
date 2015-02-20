package net.blay09.mods.eirairc.bot;

import cpw.mods.fml.common.eventhandler.Event;
import net.blay09.mods.eirairc.api.bot.IRCBot;

/**
 * Created by Blay09 on 20.02.2015.
 */
public class ReloadBotCommandsEvent extends Event {
	public final IRCBot bot;

	public ReloadBotCommandsEvent(IRCBot bot) {
		this.bot = bot;
	}
}
