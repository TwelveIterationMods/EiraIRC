// Copyright (c) 2014, Christopher "blay09" Baker

package net.blay09.mods.eirairc.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.blay09.mods.eirairc.util.Utils;

public class IRCConnection implements Runnable {

	public static final int IRC_DEFAULT_PORT = 6667;
	public static final String EMOTE_START = "\u0001ACTION ";
	public static final String EMOTE_END = "\u0001";
	private static final String DEFAULT_IDENT = "EiraIRC";
	private static final String DEFAULT_DESCRIPTION = "EiraIRC Bot";
	private static final String LINE_FEED = "\r\n";
	
	private final int port;
	private final String host;
	private final String password;
	private String nick;
	private String ident;
	private String description;
	private String charset;
	private boolean connected;
	private final IRCParser parser = new IRCParser();
	private IIRCEventHandler eventHandler;
	private IIRCConnectionHandler connectionHandler;
	private final Map<String, IRCChannel> channels = new HashMap<String, IRCChannel>();
	private final Map<String, IRCUser> users = new HashMap<String, IRCUser>();
	
	private Thread thread;
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	
	public IRCConnection(String host, String nick) {
		this(host, IRC_DEFAULT_PORT, nick);
	}
	
	public IRCConnection(String host, int port, String nick) {
		this(host, port, null, nick);
	}
	
	public IRCConnection(String host, String password, String nick) {
		this(host, IRC_DEFAULT_PORT, password, nick);
	}
	
	public IRCConnection(String host, int port, String password, String nick) {
		this(host, port, password, nick, DEFAULT_IDENT, DEFAULT_DESCRIPTION);
	}
	
	public IRCConnection(String host, int port, String password, String nick, String ident, String description) {
		this.host = host;
		this.port = port;
		this.password = password;
		this.nick = nick;
		this.ident = ident;
		this.description = description;
	}
	
	public void setEventHandler(IIRCEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	public void setConnectionHandler(IIRCConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}
	
	public void setLogin(String login) {
		this.ident = login;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public String getNick() {
		return nick;
	}
	
	public IRCChannel getChannel(String channelName) {
		return channels.get(channelName.toLowerCase());
	}
	
	public IRCChannel getOrCreateChannel(String channelName) {
		IRCChannel channel = getChannel(channelName);
		if(channel == null) {
			channel = new IRCChannel(this, channelName);
			channels.put(channelName.toLowerCase(), channel);
		}
		return channel;
	}
	
	public IRCUser getUser(String nick) {
		return users.get(nick.toLowerCase());
	}
	
	public IRCUser getOrCreateUser(String nick) {
		IRCUser user = getUser(nick);
		if(user == null) {
			user = new IRCUser(this, nick);
			users.put(nick.toLowerCase(), user);
		}
		return user;
	}

	public String getHost() {
		return host;
	}
	
	public Collection<IRCChannel> getChannels() {
		return channels.values();
	}
	
	public boolean connect() {
		try {
			socket = new Socket(host, port);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charset));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset));
			connectionHandler.onConnecting(this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		thread = new Thread(this);
		thread.start();
		return true;
	}
	
	@Override
	public void run() {
		try {
			register();
			String line = null;
			while((line = reader.readLine()) != null) {
				IRCMessage msg = parser.parse(line);
				if(handleNumericMessage(msg)) {
					continue;
				} else if(handleMessage(msg)) {
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		tryReconnect();
	}
	
	public void tryReconnect() {
		connectionHandler.onDisconnected(this);
		if(connected) {
			if(connect()) {
				connectionHandler.onConnecting(this);
			}
		}
	}
	
	public void disconnect(String quitMessage) {
		try {
			connected = false;
			if(socket != null) {
				writer.write("QUIT :" + quitMessage + "\r\n");
				writer.flush();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void register() {
		try {
			if(password != null && !password.isEmpty()) {
				writer.write("PASS " + password + "\r\n");
			}
			writer.write("NICK " + nick + "\r\n");
			writer.write("USER " + ident + " \"\" \"\" :" + description + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			tryReconnect();
		}
	}
	
	public void nick(String nick) {
		if(sendIRC("NICK " + nick)) {
			this.nick = nick;
		}
	}
	
	public void join(String channelName, String channelKey) {
		sendIRC("JOIN " + channelName + (channelKey != null ? (" " + channelKey) : ""));
	}
	
	public void part(String channelName) {
		if(sendIRC("PART " + channelName)) {
			IRCChannel channel = getChannel(channelName);
			if(channel != null) {
				connectionHandler.onChannelLeft(this, channel);
			}
			channels.remove(channelName.toLowerCase());
		}
	}
	
	public void mode(String targetName, String flags) {
		sendIRC("MODE " + targetName + " " + flags);
	}
	
	public void mode(String targetName, String flags, String nick) {
		sendIRC("MODE " + targetName + " " + flags + " " + nick);
	}
	
	public void topic(String channelName) {
		sendIRC("TOPIC " + channelName);
	}
	
	public void topic(String channelName, String topic) {
		sendIRC("TOPIC " + channelName + " :" + topic);
	}
	
	private boolean handleNumericMessage(IRCMessage msg) {
		int numeric = msg.getNumericCommand();
		if(numeric == -1) {
			return false;
		}
		if(numeric == IRCReplyCodes.RPL_NAMREPLY) {
			IRCChannel channel = getChannel(msg.arg(2));
			String[] names = msg.arg(3).split(" ");
			for(int i = 0; i < names.length; i++) {
				String name = names[i];
				if(name.startsWith("@") || name.startsWith("+")) {
					name = name.substring(1);
				}
				IRCUser user = getOrCreateUser(name);
				user.addChannel(channel);
				channel.addUser(user);
			}
			connectionHandler.onChannelJoined(this, channel);
		} else if(numeric == IRCReplyCodes.RPL_ENDOFMOTD) {
			connected = true;
			connectionHandler.onConnected(this);
		} else if(numeric == IRCReplyCodes.RPL_TOPIC) {
			IRCChannel channel = getChannel(msg.arg(0));
			if(channel != null) {
				channel.setTopic(msg.arg(1));
				eventHandler.onTopicChange(channel, channel.getTopic());
			}
		} else if(numeric == IRCReplyCodes.RPL_WHOISLOGIN) {
			IRCUser user = getOrCreateUser(msg.arg(1));
			user.setAuthLogin(msg.arg(2));
		} else if(numeric == IRCReplyCodes.ERR_NICKNAMEINUSE || numeric == IRCReplyCodes.ERR_ERRONEUSNICKNAME) {
			connectionHandler.onIRCError(this, msg);
		} else if(numeric == IRCReplyCodes.RPL_MOTD) {
			// ignore
		} else if(numeric <= 5 || numeric == 251 || numeric == 252 || numeric == 254 || numeric == 255 || numeric == 265 || numeric == 266 || numeric == 250 || numeric == 375) {
			// ignore for now
		} else {
			System.out.println("Unhandled message code: " + msg.getCommand() + " (" + msg.argcount() + " arguments)");
		}
		return true;
	}
	
	private boolean handleMessage(IRCMessage msg) {
		String cmd = msg.getCommand();
		if(cmd.equals("PING")) {
			sendIRC("PONG " + msg.arg(0));
		} else if(cmd.equals("PRIVMSG")) {
			IRCUser user = getOrCreateUser(msg.getNick());
			String target = msg.arg(0);
			String message = msg.arg(1);
			boolean isEmote = false;
			if(message.startsWith(EMOTE_START)) {
				message = message.substring(EMOTE_START.length(), message.length() - EMOTE_END.length());
				isEmote = true;
			}
			if(target.startsWith("#")) {
				IRCChannel channel = getChannel(target);
				if(isEmote) {
					eventHandler.onChannelEmote(this, channel, user, message);
				} else {
					eventHandler.onChannelMessage(this, channel, user, message);
				}
			} else if(target.equals(this.nick)) {
				if(isEmote) {
					eventHandler.onPrivateEmote(this, user, message);
				} else {
					eventHandler.onPrivateMessage(this, user, message);
				}
			}
		} else if(cmd.equals("JOIN")) {
			IRCUser user = getOrCreateUser(msg.getNick());
			IRCChannel channel = getOrCreateChannel(msg.arg(0));
			channel.addUser(user);
			user.addChannel(channel);
			eventHandler.onUserJoin(this, user, channel);
		} else if(cmd.equals("PART")) {
			IRCUser user = getOrCreateUser(msg.getNick());
			IRCChannel channel = getChannel(msg.arg(0));
			if(channel != null) {
				channel.removeUser(user);
				user.removeChannel(channel);
				eventHandler.onUserPart(this, user, channel, msg.arg(1));
			}
		} else if(cmd.equals("NICK")) {
			String newNick = msg.arg(0);
			IRCUser user = getOrCreateUser(msg.getNick());
			eventHandler.onNickChange(this, user, newNick);
			users.remove(user.getName().toLowerCase());
			user.setName(newNick);
			users.put(user.getName().toLowerCase(), user);
		} else if(cmd.equals("QUIT")) {
			IRCUser user = getOrCreateUser(msg.getNick());
			eventHandler.onUserQuit(this, user, msg.arg(0));
			for(IRCChannel channel : user.getChannels()) {
				channel.removeUser(user);
			}
			users.remove(user.getName().toLowerCase());
		}
		return false;
	}

	public void whois(String nick) {
		sendIRC("WHOIS " + nick);
	}
	
	public void sendMessage(String target, String message) {
		sendIRC("PRIVMSG " + target + " :" + message);
	}

	public void sendPrivateMessage(IRCUser user, String message) {
		if(user != null) {
			sendMessage(user.getName(), message);
		}
	}

	public void sendChannelMessage(IRCChannel channel, String message) {
		if(channel != null) {
			sendMessage(channel.getName(), message);
		}
	}

	public void sendNotice(String target, String message) {
		sendIRC("NOTICE " + target + " :" + message);
	}
	
	public void sendPrivateNotice(IRCUser user, String message) {
		sendNotice(user.getName(), message);
	}
	
	public void sendChannelNotice(IRCChannel channel, String message) {
		sendNotice(channel.getName(), message);
	}

	public void kick(String channelName, String nick, String reason) {
		sendIRC("KICK " + channelName + " " + nick + (reason != null ? (" :" + reason) : ""));
	}
	
	public boolean sendIRC(String message) {
		try {
			writer.write(message);
			writer.write(LINE_FEED);
			writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			tryReconnect();
			return false;
		}
	}

	public IRCChannel getDefaultChannel() {
		Iterator<IRCChannel> it = channels.values().iterator();
		if(it.hasNext()) {
			return it.next();
		}
		return null;
	}

}
