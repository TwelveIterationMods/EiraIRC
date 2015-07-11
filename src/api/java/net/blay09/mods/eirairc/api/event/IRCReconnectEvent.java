// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCConnection;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever EiraIRC attempts to reconnect to IRC after the connection was lost.
 */
public class IRCReconnectEvent extends IRCEvent {

    /**
     * the amount of milliseconds EiraIRC will wait before attempting to reeconnect
     */
    public final int waitingTime;

    /**
     * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
     * @param connection the connection that is being reconnected to
     * @param waitingTime the amount of milliseconds EiraIRC will wait before attempting to reeconnect
     */
    public IRCReconnectEvent(IRCConnection connection, int waitingTime) {
        super(connection);
        this.waitingTime = waitingTime;
    }

}
