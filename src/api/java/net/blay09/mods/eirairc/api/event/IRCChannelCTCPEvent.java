package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * @author soniex2
 */
@Cancelable
public class IRCChannelCTCPEvent extends IRCMessageEvent {
	/**
	 * the channel this IRC message came from
	 */
	public final IRCChannel channel;

	/**
	 * the user that sent this IRC message
	 */
	public final IRCUser sender;

	/**
	 * the message that was sent
	 */
	public final String message;

	/**
	 * true, fi this message was sent as a NOTICE
	 */
	public final boolean isNotice;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this IRC message came from
	 * @param channel the channel this IRC message came from
	 * @param sender the user that sent this IRC message
	 * @param rawMessage the raw IRC message that was sent
	 * @param message the message that was sent
	 * @param isNotice true, if this message was sent as a NOTICE
	 */
	public IRCChannelCTCPEvent(IRCConnection connection, IRCChannel channel, IRCUser sender, IRCMessage rawMessage, String message, boolean isNotice) {
		super(connection, rawMessage);
		this.channel = channel;
		this.sender = sender;
		this.message = message;
		this.isNotice = isNotice;
	}
}
