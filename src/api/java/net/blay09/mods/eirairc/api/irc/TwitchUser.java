package net.blay09.mods.eirairc.api.irc;

/**
 * This can be retrieved by calling getTwitchUser() on an IRCUser from a Twitch connection.
 */
public interface TwitchUser {
    /**
     * @param channel a Twitch channel
     * @return true if this user is a subscriber in the given channel
     */
    boolean isTwitchSubscriber(IRCChannel channel);

    /**
     * @return true if this user is publically Twitch Turbo
     */
    boolean isTwitchTurbo();
}
