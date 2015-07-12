package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * Base class for events based on a PRIVMSG message. It is "IRCChatOrCTCPEvent" because "IRCMessageEvent" was already taken.
 *
 * @author soniex2
 */
@Cancelable
public abstract class IRCChatOrCTCPEvent extends IRCMessageEvent {

    /**
     * the user that sent this IRC message
     */
    public final IRCUser sender;

    /**
     * the message that was sent
     */
    public final String message;

    /**
     * true, if this message was sent as a NOTICE
     */
    public final boolean isNotice;

    /**
     * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
     *
     * @param connection the connection this event is based on
     * @param sender     the user that sent this IRC message
     * @param rawMessage the raw IRC message that was sent
     * @param message    the message that was sent
     * @param isNotice   true, if this message was sent as a NOTICE
     */
    public IRCChatOrCTCPEvent(IRCConnection connection, IRCMessage rawMessage, IRCUser sender, String message, boolean isNotice) {
        super(connection, rawMessage);
        this.sender = sender;
        this.message = message;
        this.isNotice = isNotice;
    }
}
