package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * @author soniex2
 */
public class IRCChannelCTCPEvent extends IRCChannelMessageEvent {
    /**
     * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
     *
     * @param connection the connection this IRC message came from
     * @param channel    the channel this IRC message came from
     * @param sender     the user that sent this IRC message
     * @param rawMessage the raw IRC message that was sent
     * @param message    the message that was sent
     * @param isNotice   true, if this message was sent as a NOTICE
     */
    public IRCChannelCTCPEvent(IRCConnection connection, IRCChannel channel, IRCUser sender, IRCMessage rawMessage, String message, boolean isNotice) {
        super(connection, rawMessage, sender, message, isNotice, channel);
    }
}
