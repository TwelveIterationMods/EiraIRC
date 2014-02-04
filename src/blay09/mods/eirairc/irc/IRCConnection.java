// Copyright (c) 2013, Christopher "blay09" Baker

package blay09.mods.eirairc.irc;

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

import blay09.mods.eirairc.util.Utils;

public class IRCConnection implements Runnable {

	public static final int IRC_DEFAULT_PORT = 6667;
	public static final String EMOTE_START = "\u0001ACTION ";
	public static final String EMOTE_END = "\u0001";
	private static final String DEFAULT_LOGIN = "EiraIRC";
	private static final String DEFAULT_DESCRIPTION = "EiraIRC Bot";
	private static final String LINE_FEED = "\r\n";
	
	private final int port;
	private final String host;
	private final String password;
	private String nick;
	private String login;
	private String description;
	private String charset;
	private boolean connected;
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
		this.host = host;
		this.port = port;
		this.password = password;
		this.nick = nick;
		this.login = DEFAULT_LOGIN;
		this.description = DEFAULT_DESCRIPTION;
	}
	
	public void setEventHandler(IIRCEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	public void setConnectionHandler(IIRCConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}
	
	public void setLogin(String login) {
		this.login = login;
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
		return channels.get(channelName);
	}
	
	public IRCUser getUser(String nick) {
		return users.get(nick);
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
				String[] cmd = line.split(" ");
				for(int i = 0; i < cmd.length; i++) {
					if(cmd[i].startsWith(":")) {
						cmd[i] = cmd[i].substring(1);
					}
				}
				if(handlePing(line, cmd)) {
					continue;
				}
				if(handleNumericReply(line, cmd)) {
					continue;
				}
				if(handleMessage(line, cmd)) {
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
			writer.write("USER " + login + " \"\" \"\" :" + description + "\r\n");
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
			channels.remove(channelName);
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
	
	private boolean handlePing(String line, String[] cmd) throws IOException {
		if(cmd[0].equals("PING")) {
			writer.write("PONG " + line.substring(5) + "\r\n");
			writer.flush();
			return true;
		}
		return false;
	}
	
	private boolean handleNumericReply(String line, String[] cmd) {
		if(cmd.length > 1) {
			String nr = cmd[1];
			try {
				int replyCode = Integer.parseInt(nr);
				return handleNumericReply(replyCode, line, cmd);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}
	
	private boolean handleNumericReply(int replyCode, String line, String[] cmd) {
		IRCChannel channel = null;
		IRCUser user = null;
		switch(replyCode) {
		case IRCReplyCodes.RPL_NAMREPLY:
			channel = getChannel(cmd[4]);
			
			String nameList = line.substring(line.lastIndexOf(":") + 1);
			String[] names = nameList.split(" ");
			for(int i = 0; i < names.length; i++) {
				String name = names[i];
				if(name.startsWith("@")) {
					// TODO mark user as op for channel
					name = name.substring(1);
				} else if(name.startsWith("+")) {
					// TODO mark user as voiced for channel
					name = name.substring(1);
				}
				user = channel.getUser(name);
				if(user == null) {
					user = new IRCUser(this, name);
					users.put(user.getName(), user);
					user.addChannel(channel);
					channel.addUser(user);
				}
			}
			connectionHandler.onChannelJoined(this, channel);
			break;
		case IRCReplyCodes.RPL_ENDOFMOTD:
			connected = true;
			connectionHandler.onConnected(this);
			break;
		case IRCReplyCodes.RPL_TOPIC:
			channel = getChannel(cmd[3]);
			if(channel != null) {
				int textIdx = line.indexOf(':', 1);
				String topic = line.substring(textIdx + 1);
				channel.setTopic(topic);
				eventHandler.onTopicChange(channel, topic);
			}
			break;
		case IRCReplyCodes.RPL_WHOISLOGIN:
			user = users.get(cmd[3]);
			if(user == null) {
				user = new IRCUser(this, cmd[3]);
				users.put(user.getName(), user);
			}
			user.setAuthLogin(cmd[4]);
			break;
		case IRCReplyCodes.ERR_ERRONEUSNICKNAME:
		case IRCReplyCodes.ERR_NICKNAMEINUSE:
		case IRCReplyCodes.ERR_NICKCOLLISION:
		case IRCReplyCodes.ERR_BANNEDFROMCHAN:
		case IRCReplyCodes.ERR_INVITEONLYCHAN:
		case IRCReplyCodes.ERR_BADCHANNELKEY:
		case IRCReplyCodes.ERR_CHANNELISFULL:
		case IRCReplyCodes.ERR_NOSUCHCHANNEL:
		case IRCReplyCodes.ERR_BADCHANMASK:
		case IRCReplyCodes.ERR_TOOMANYCHANNELS:
		case IRCReplyCodes.ERR_NOTONCHANNEL:
		case IRCReplyCodes.ERR_ALREADYREGISTRED:
		case IRCReplyCodes.ERR_NEEDMOREPARAMS:
		case IRCReplyCodes.ERR_CHANOPRIVSNEEDED:
		case IRCReplyCodes.ERR_NOSUCHNICK:
		case IRCReplyCodes.ERR_CANNOTSENDTOCHAN:
			connectionHandler.onIRCError(this, replyCode, line, cmd);
			break;
		default:
			System.out.println("Unhandled reply code: " + replyCode + " (" + line + ")");
			break;
		}
		return false;
	}
	
	private boolean handleMessage(String line, String[] cmd) {
		String nick = Utils.getNickFromUser(cmd[0]);
		String msg = cmd[1];
		if(msg.equals("PRIVMSG")) {
			IRCUser user = users.get(nick);
			if(user == null) {
				user = new IRCUser(this, nick);
				users.put(nick, user);
			}
			String target = cmd[2];
			int messageIdx = line.indexOf(":", 1) + 1;
			String message = line.substring(messageIdx);
			boolean isEmote = false;
			if(message.contains(EMOTE_START)) {
				message = line.substring(messageIdx + 8, line.length() - 1);
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
		} else if(msg.equals("JOIN")) {
			IRCUser user = users.get(nick);
			if(user == null) {
				user = new IRCUser(this, nick);
				users.put(nick, user);
			}
			IRCChannel channel = getChannel(cmd[2]);
			if(channel == null) {
				channel = new IRCChannel(this, cmd[2]);
				channels.put(cmd[2], channel);
			}
			channel.addUser(user);
			user.addChannel(channel);
			eventHandler.onUserJoin(this, user, channel);
		} else if(msg.equals("PART")) {
			IRCUser user = users.get(nick);
			if(user == null) {
				user = new IRCUser(this, nick);
				users.put(nick, user);
			}
			IRCChannel channel = getChannel(cmd[2]);
			if(channel != null) {
				channel.removeUser(nick);
				int quitMessageIdx = cmd[0].length() + 6 + cmd[2].length() + 2;
				String quitMessage = null;
				if(line.length() >= quitMessageIdx) {
					quitMessage = line.substring(quitMessageIdx);
				}
				eventHandler.onUserPart(this, user, channel, quitMessage);
			}
		} else if(msg.equals("NICK")) {
			String newNick = cmd[2];
			IRCUser user = users.get(nick);
			if(user == null) {
				user = new IRCUser(this, nick);
			}
			eventHandler.onNickChange(this, user, newNick);
			users.remove(user.getName());
			user.setName(newNick);
			users.put(user.getName(), user);
		} else if(msg.equals("QUIT")) {
			int quitMessageIdx = cmd[0].length() + 8;
			String quitMessage = null;
			if(line.length() >= quitMessageIdx) {
				quitMessage = line.substring(cmd[0].length() + 7);
			}
			IRCUser user = users.get(nick);
			if(user == null) {
				user = new IRCUser(this, nick);
				users.put(nick, user);
			}
			eventHandler.onUserQuit(this, user, quitMessage);
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
