// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.irc;

public class IRCMessage {

	private String prefix;
	private String command;
	private String[] args;
	
	public IRCMessage(String prefix, String command, String[] args) {
		this.prefix = prefix;
		this.command = command;
		this.args = args;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getCommand() {
		return command;
	}
	
	public int getNumericCommand() {
		try {
			return Integer.parseInt(command);
		} catch (NumberFormatException e) { }
		return -1;
	}
	
	public String getHostName() {
		int idx = prefix.indexOf('@');
		if(idx != -1 && idx + 1 < prefix.length()) {
			return prefix.substring(idx + 1);
		}
		return null;
	}
	
	public String getUsername() {
		int start = prefix.indexOf('!');
		int end = prefix.indexOf('@');
		if(end == -1) {
			end = prefix.length() - 1;
		}
		if(start != -1 && start + 1 < prefix.length()) {
			return prefix.substring(start + 1, end);
		}
		return null;
	}
	
	public String getNick() {
		int end = prefix.indexOf('!');
		if(end != -1) {
			return prefix.substring(0, end);
		}
		return null;
	}
	
	public String arg(int idx) {
		if(idx >= args.length) {
			return null;
		}
		return args[idx];
	}
	
	public String[] args() {
		return args;
	}

	public int argcount() {
		return args.length;
	}
}
