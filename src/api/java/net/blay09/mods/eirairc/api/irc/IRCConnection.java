// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.irc;

import net.blay09.mods.eirairc.api.bot.IRCBot;

import java.util.Collection;

public interface IRCConnection extends IRCContext {

	/**
	 * Sends a raw IRC protocol message to the server.
	 * @param irc message
	 * @return true if succeeded
	 */
	boolean irc(String irc);

	/**
	 * Changes the nick of this connection to the specified one.
	 * Results in a {@code IRCUserNickChangeEvent} or {@code IRCErrorEvent} with a numeric of ERR_NICKNAMEINUSE, ERR_ERRONEUSNICKNAME or ERR_NICKCOLLISION
	 * IRC Command: NICK nick
	 * @param nick new nick for this connection
	 */
	void nick(String nick);

	/**
	 * Joins an IRC channel with an optional password.
	 * Results in a {@code IRCChannelJoinedEvent} or {@code IRCErrorEvent}
	 * IRC Command: JOIN channelName [password]
	 * @param channelName name of the channel including the channel prefix
	 * @param password password to the channel or null if none
	 */
	void join(String channelName, String password);

	/**
	 * Leaves an IRC channel.
	 * Results in a {@code IRCChannelLeftEvent} or {@code IRCErrorEvent}
	 * IRC Command: PART channelName
	 * @param channelName name of the channel including the channel prefix
	 */
	void part(String channelName);

	/**
	 * Kicks an IRC user from the specified channel with an optional reason.
	 * IRC Command: KICK channelName nick [:reason]
	 * @param channelName name of the channel including the channel prefix
	 * @param nick the nick of the user to be kicked
	 * @param reason an optional reason or null
	 */
	void kick(String channelName, String nick, String reason);

	/**
	 * Requires this connection to be OP in this channel.
	 * IRC Command: MODE channelName mode
	 * @param channelName name of the channel including the prefix
	 * @param mode an IRC mode string
	 */
	void mode(String channelName, String mode);

	/**
	 * Requires this connection to be OP in this channel.
	 * IRC Command: MODE channelName nick mode
	 * @param channelName name of the channel including the prefix
	 * @param nick nick of the user that is being edited
	 * @param mode an IRC mode string
	 */
	void mode(String channelName, String nick, String mode);

	/**
	 * Requires this connection to be OP in this channel.
	 * IRC Command: TOPIC channelName :topic
	 * @param channelName name of the channel including the prefix
	 * @param topic the new topic to be set for this channel
	 */
	void topic(String channelName, String topic);

	/**
	 * IRC Command: QUIT :quitMessage
	 * @param quitMessage the message to be shown on the quit notification
	 */
	void disconnect(String quitMessage);

	/**
	 * Currently not implemented. Supposed to return the server architecture of the connected IRC server in the future.
	 * @deprecated This function is not implemented yet and will always return null.
	 * @return the server architecture used
	 */
	@Deprecated
	String getServerType();

	/**
	 * @return a string containing all channel prefixes this IRC server supports, one per character.
	 */
	String getChannelTypes();

	/**
	 * @return a string containing all channel member modes this IRC server supports, one per character.
	 */
	String getChannelUserModes();

	/**
	 * @return a string containing the prefixes of all channel member modes this IRC server supports, one per character.
	 */
	String getChannelUserModePrefixes();

	/**
	 * @return a list of all channels this connection is currently part of
	 */
	Collection<IRCChannel> getChannels();

	/**
	 * @param name the name of the channel to be returned, including the prefix
	 * @return an IRCChannel object for the specified name or null, if this channel is not joined
	 */
	IRCChannel getChannel(String name);

	/**
	 * @param name the nick of the user to be returned
	 * @return an IRCUser object for the specified nick or null, if this user is not known to this connection yet
	 */
	IRCUser getUser(String name);

	/**
	 * @param name the nick of the user to be returned
	 * @return an IRCUser object for the specified nick, even if the connection doesn't know of this user yet
	 */
	IRCUser getOrCreateUser(String name);

	/**
	 * @return the server address of this IRC server with the port stripped from it
	 */
	String getHost();

	/**
	 * @return an array of the ports this connection will try to connect to
	 */
	int[] getPorts();

	/**
	 * @return the bot sitting on top of this connection
	 */
	IRCBot getBot();

	/**
	 * @return the nick that is currently being used on this connection
	 */
	String getNick();

	/**
	 * @return the IRCUser object that represents the connection's bot
	 */
	IRCUser getBotUser();

	/**
	 * @return true if this is a Twitch chat connection
	 */
	boolean isTwitch();
}
