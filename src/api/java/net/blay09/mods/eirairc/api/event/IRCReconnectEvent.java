package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;

/**
 * Created by Blay09 on 06.04.2015.
 */
public class IRCReconnectEvent extends IRCEvent {

    public final int waitingTime;

    public IRCReconnectEvent(IRCConnection connection, int waitingTime) {
        super(connection);
        this.waitingTime = waitingTime;
    }

}
