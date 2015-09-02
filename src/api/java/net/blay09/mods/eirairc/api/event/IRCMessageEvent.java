package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Base class for events based on a PRIVMSG message. It is "IRCChatOrCTCPEvent" because "IRCMessageEvent" was already taken.
 *
 * @author soniex2
 */
@Cancelable
public abstract class IRCMessageEvent extends IRCRawMessageEvent {

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
    public IRCMessageEvent(IRCConnection connection, IRCMessage rawMessage, IRCUser sender, String message, boolean isNotice) {
        super(connection, rawMessage);
        this.sender = sender;
        this.message = message;
        this.isNotice = isNotice;
    }
}
