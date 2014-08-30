// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import java.util.ArrayList;
import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.irc.IRCChannelImpl;
import net.blay09.mods.eirairc.irc.IRCUserImpl;

public class ChatSessionHandler {

	private String chatTarget = null;
	private final List<IRCChannel> validTargetChannels = new ArrayList<IRCChannel>();
	private final List<IRCUser> validTargetUsers = new ArrayList<IRCUser>();
	private int targetChannelIdx = 0;
	private int targetUserIdx = -1;
	
	public String getChatTarget() {
		return chatTarget;
	}
	
	public void addTargetUser(IRCUser user) {
		if(!validTargetUsers.contains(user)) {
			validTargetUsers.add(user);
		}
	}
	
	public void addTargetChannel(IRCChannel channel) {
		if(!validTargetChannels.contains(channel)) {
			validTargetChannels.add(channel);
		}
	}
	
	public void removeTargetUser(IRCUser user) {
		validTargetUsers.remove(user);
	}
	
	public void removeTargetChannel(IRCChannel channel) {
		validTargetChannels.remove(channel);
	}
	
	public void setChatTarget(String chatTarget) {
		this.chatTarget = chatTarget;
	}
	
	public void setChatTarget(IRCUserImpl user) {
		this.chatTarget = user.getIdentifier();
	}
	
	public void setChatTarget(IRCChannelImpl channel) {
		this.chatTarget = channel.getIdentifier();
	}
	
	public boolean isChannelTarget() {
		return chatTarget.contains("#");
	}
	
	public boolean isUserTarget() {
		return !isChannelTarget();
	}
	
	public String getNextTarget(boolean users) {
		if(users) {
			if(validTargetUsers.isEmpty()) {
				return null;
			}
			targetUserIdx++;
			if(targetUserIdx >= validTargetUsers.size()) {
				targetUserIdx = 0;
			}
			return validTargetUsers.get(targetUserIdx).getIdentifier();
		} else {
			if(validTargetChannels.isEmpty()) {
				return null;
			}
			targetChannelIdx++;
			if(targetChannelIdx > validTargetChannels.size()) {
				targetChannelIdx = 0;
			}
			if(targetChannelIdx == 0) {
				return null;
			}
			return validTargetChannels.get(targetChannelIdx - 1).getIdentifier();
		}
	}

	public IRCContext getIRCTarget() {
		if(chatTarget == null) {
			return null;
		}
		int sepIdx = chatTarget.indexOf('/');
		String targetHost = chatTarget.substring(0, sepIdx);
		IRCConnection connection = EiraIRC.instance.getConnection(targetHost);
		if(connection == null) {
			return null;
		}
		String target = chatTarget.substring(sepIdx + 1); 
		if(isChannelTarget()) {
			return connection.getChannel(target);
		} else {
			return connection.getUser(target);
		}
	}

	public boolean isMinecraftTarget() {
		return chatTarget == null;
	}

}
