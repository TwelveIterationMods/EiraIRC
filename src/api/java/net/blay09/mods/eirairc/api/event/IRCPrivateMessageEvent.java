package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * @author soniex2
 */
public abstract class IRCPrivateMessageEvent extends IRCMessageEvent {
    /**
     * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
     *
     * @param connection the connection this event is based on
     * @param rawMessage the raw IRC message that was sent
     * @param sender     the user that sent this IRC message
     * @param message    the message that was sent
     * @param isNotice   true, if this message was sent as a NOTICE
     */
    public IRCPrivateMessageEvent(IRCConnection connection, IRCMessage rawMessage, IRCUser sender, String message, boolean isNotice) {
        super(connection, rawMessage, sender, message, isNotice);
    }
}
