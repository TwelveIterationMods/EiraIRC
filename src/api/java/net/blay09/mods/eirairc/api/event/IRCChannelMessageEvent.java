package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * Base class for events based on a PRIVMSG message to a channel.
 *
 * @author soniex2
 */
public abstract class IRCChannelMessageEvent extends IRCMessageEvent {

    /**
     * the channel this IRC message came from
     */
    public final IRCChannel channel;

    /**
     * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
     *
     * @param connection the connection this event is based on
     * @param rawMessage the raw IRC message that was sent
     * @param sender     the user that sent this IRC message
     * @param message    the message that was sent
     * @param isNotice   true, if this message was sent as a NOTICE
     * @param channel    the channel this IRC message came from
     */
    public IRCChannelMessageEvent(IRCConnection connection, IRCMessage rawMessage, IRCUser sender, String message, boolean isNotice, IRCChannel channel) {
        super(connection, rawMessage, sender, message, isNotice);
        this.channel = channel;
    }
}
