// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.irc;

public interface IRCContext {

	/**
	 * Defines the type of an IRC context.
	 */
	enum ContextType {
		Error,
		IRCConnection,
		IRCChannel,
		IRCUser
	}

	/**
	 * Examples for return values are #EiraIRC, irc.esper.net or BlayTheNinth
	 * @return the name of this context or in case of an error, the corresponding language key
	 */
	String getName();

	/**
	 * @return the type of this IRC context
	 */
	ContextType getContextType();

	/**
	 * Examples for return values are irc.esper.net/#EiraIRC or irc.epser.net/BlayTheNinth
	 * @return the unique identifier for this context
	 */
	String getIdentifier();

	/**
	 * @return the connection this context is based on or null if of type Error
	 */
	IRCConnection getConnection();

	/**
	 * Sends a message to this IRC context.
	 * Raw IRC Equivalent: PRIVMSG getName() :message
	 * Does nothing for types IRCConnection and Error.
	 * @param message the message to be sent to this context
	 */
	void message(String message);

	/**
	 * Sends a notice to this IRC context.
	 * Raw IRC Equivalent: NOTICE getName() :message
	 * Does nothing for types IRCConnection and Error.
	 * @param message the message to be sent to this context
	 */
	void notice(String message);

	/**
	 * Sends a CTCP message to this IRC context.
	 * Raw IRC Equivalent: PRIVMSG getName() :\001message\001
	 * Does nothing for types IRCConnection and Error.
	 * @param message the message to be sent to this context
	 */
	void ctcpMessage(String message);

	/**
	 * Sends a CTCP notice to this IRC context.
	 * Raw IRC Equivalent: NOTICE getName() :\001message\001
	 * Does nothing for types IRCConnection and Error.
	 * @param message the message to be sent to this context
	 */
	void ctcpNotice(String message);
	
}
