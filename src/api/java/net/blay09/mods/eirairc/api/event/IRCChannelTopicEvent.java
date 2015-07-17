// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever the topic of a channel changes.
 * It is also published once when joining a channel, if a topic is set.
 */
public class IRCChannelTopicEvent extends IRCMessageEvent {

	/**
	 * the channel that had it's topic changed
	 */
	public final IRCChannel channel;

	/**
	 * the user that changed the topic
	 */
	public final IRCUser user;

	/**
	 * the new topic
	 */
	public final String topic;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection the channel that was left is on
	 * @param channel the channel that was left
	 */
	public IRCChannelTopicEvent(IRCConnection connection, IRCMessage rawMessage, IRCChannel channel, IRCUser user, String topic) {
		super(connection, rawMessage);
		this.channel = channel;
		this.user = user;
		this.topic = topic;
	}
}
