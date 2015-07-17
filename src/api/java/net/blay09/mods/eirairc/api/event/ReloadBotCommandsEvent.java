// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever EiraIRC reloads it's configuration.
 * It is also published once during startup.
 * Other mods can listen on this event to register their own custom bot events.
 */
public class ReloadBotCommandsEvent extends Event {

	/**
	 * the bot reloading it's command register
	 */
	public final IRCBot bot;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param bot the bot reloading it's command register
	 */
	public ReloadBotCommandsEvent(IRCBot bot) {
		this.bot = bot;
	}
}
