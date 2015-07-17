// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus when chat messages are being sent to IRC.
 * Other mods can use this event to send custom messages to IRC or control which messages are sent.
 * If this event is cancelled, the message will not be sent to IRC.
 * @deprecated It is recommended to use the relayChat method of {@code EiraIRCAPI} instead.
 */
@Cancelable
@Deprecated
public class RelayChat extends Event {

	/**
	 * the IRC context of type Channel or User this message should be sent to or null if the active context should be used
	 */
	public final IRCContext target;

	/**
	 * the player that sent this message or null if in singleplayer
	 */
	public final ICommandSender sender;

	/**
	 * the message that should be sent to IRC
	 */
	public final String message;

	/**
	 * true, if this message should be sent as an emote
	 */
	public final boolean isEmote;

	/**
	 * true, if this message should be sent as a NOTICE
	 */
	public final boolean isNotice;

	/**
	 * Requests a normal (non-emote, non-notice) message to be sent to the currently active IRC context.
	 * @param sender the player that sent this message or null if in singleplayer
	 * @param message the message that should be sent to the active IRC context
	 */
	public RelayChat(ICommandSender sender, String message) {
		this(sender, message, false, false, null);
	}

	/**
	 * Requests a non-notice message to be sent to the currently active IRC context.
	 * @param sender the player that sent this message or null if in singleplayer
	 * @param message the message that should be sent to the active IRC context
	 * @param isEmote true, if this message should be sent as an emote
	 */
	public RelayChat(ICommandSender sender, String message, boolean isEmote) {
		this(sender, message, isEmote, false, null);
	}

	/**
	 * Requests a message to be sent to the currently active IRC context.
	 * @param sender the player that sent this message or null if in singleplayer
	 * @param message the message that should be sent to the active IRC context
	 * @param isEmote true, if this message should be sent as an emote
	 * @param isNotice true, if this message should be sent as a NOTICE
	 */
	public RelayChat(ICommandSender sender, String message, boolean isEmote, boolean isNotice) {
		this(sender, message, isEmote, isNotice, null);
	}

	/**
	 * Requests a message to be sent to the given IRC context.
	 * @param sender the player that sent this message or null if in singleplayer
	 * @param message the message that should be sent to the active IRC context
	 * @param isEmote true, if this message should be sent as an emote
	 * @param isNotice true, if this message should be sent as a NOTICE
	 * @param target the IRC context of type Channel or User this message should be sent to
	 */
	public RelayChat(ICommandSender sender, String message, boolean isEmote, boolean isNotice, IRCContext target) {
		this.sender = sender;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
		this.target = target;
	}

}
