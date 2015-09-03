package net.blay09.mods.eirairc.api.irc;

public interface TwitchUser {
    boolean isTwitchSubscriber(IRCChannel channel);
    boolean isTwitchTurbo();
}
