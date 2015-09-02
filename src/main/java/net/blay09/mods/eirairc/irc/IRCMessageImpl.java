// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.irc;

import net.blay09.mods.eirairc.api.irc.IRCMessage;

public class IRCMessageImpl implements IRCMessage {

	private final String[] tags;
	private final String prefix;
	private final String command;
	private final String[] args;
	
	public IRCMessageImpl(String[] tags, String prefix, String command, String[] args) {
		this.tags = tags;
		this.prefix = prefix;
		this.command = command;
		this.args = args;

		if(tags != null) {
			for (int i = 0; i < tags.length; i++) {
				int eqIdx = tags[i].indexOf('=');
				if (eqIdx != -1) {
					String key = tags[i].substring(0, eqIdx);
					String value = tags[i].substring(eqIdx + 1);
					value = value.replace("\\:", ";");
					value = value.replace("\\s", " ");
					value = value.replace("\\\\", "\\");
					tags[i] = key + "=" + value;
				}
			}
		}
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public int getNumericCommand() {
		try {
			return Integer.parseInt(command);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	@Override
	public String getPrefixHostname() {
		int idx = prefix.indexOf('@');
		if(idx != -1 && idx + 1 < prefix.length()) {
			return prefix.substring(idx + 1);
		}
		return null;
	}

	@Override
	public String getPrefixUsername() {
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

	@Override
	public String getPrefixNick() {
		int end = prefix.indexOf('!');
		if(end != -1) {
			return prefix.substring(0, end);
		}
		return prefix;
	}

	@Override
	public String arg(int idx) {
		if(idx >= args.length) {
			return null;
		}
		return args[idx];
	}
	
	public String[] args() {
		return args;
	}

	@Override
	public int argCount() {
		return args.length;
	}

	@Override
	public String getTagByKey(String key) {
		if(tags != null) {
			for (String tag : tags) {
				int eqIdx = tag.indexOf('=');
				if (eqIdx != -1) {
					if (tag.substring(0, eqIdx).equals(key)) {
						return tag.substring(eqIdx + 1);
					}
				}
			}
		}
		return null;
	}
}
