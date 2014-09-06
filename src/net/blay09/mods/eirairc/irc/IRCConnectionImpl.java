// Copyright (c) 2014, Christopher "blay09" Baker

package net.blay09.mods.eirairc.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.*;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.event.IRCChannelChatEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelJoinedEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelLeftEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelTopicEvent;
import net.blay09.mods.eirairc.api.event.IRCConnectEvent;
import net.blay09.mods.eirairc.api.event.IRCConnectingEvent;
import net.blay09.mods.eirairc.api.event.IRCDisconnectEvent;
import net.blay09.mods.eirairc.api.event.IRCErrorEvent;
import net.blay09.mods.eirairc.api.event.IRCPrivateChatEvent;
import net.blay09.mods.eirairc.api.event.IRCUserJoinEvent;
import net.blay09.mods.eirairc.api.event.IRCUserLeaveEvent;
import net.blay09.mods.eirairc.api.event.IRCUserNickChangeEvent;
import net.blay09.mods.eirairc.api.event.IRCUserQuitEvent;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.NetworkConfig;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.MinecraftForge;

public class IRCConnectionImpl implements Runnable, IRCConnection {

	public static class ProxyAuthenticator extends Authenticator {

		private PasswordAuthentication auth;

		public ProxyAuthenticator(String username, String password) {
			auth = new PasswordAuthentication(username, password.toCharArray());
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return auth;
		}
	}

	public static final int DEFAULT_PORT = 6667;
	public static final String EMOTE_START = "\u0001ACTION ";
	public static final String EMOTE_END = "\u0001";
	protected static final int DEFAULT_PROXY_PORT = 1080;

	private final IRCParser parser = new IRCParser();
	private final IRCSender sender = new IRCSender(this);
	private final Map<String, IRCChannel> channels = new HashMap<String, IRCChannel>();
	private final Map<String, IRCUser> users = new HashMap<String, IRCUser>();
	protected final int port;
	protected final String host;
	private final String password;
	private IRCBotImpl bot;
	private String serverType;
	private String nick;
	private String ident;
	private String description;
	protected String charset;
	private boolean connected;

	private Thread thread;
	private Socket socket;
	protected BufferedWriter writer;
	protected BufferedReader reader;
	
	public IRCConnectionImpl(String url, String password, String nick, String ident, String description) {
		this.host = Utils.extractHost(url);
		this.port = Utils.extractPort(url, DEFAULT_PORT);
		this.password = password;
		this.nick = nick;
		this.ident = ident;
		this.description = description;
	}
	
	public void setBot(IRCBotImpl bot) {
		this.bot = bot;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	@Override
	public String getNick() {
		return nick;
	}
	
	@Override
	public IRCChannel getChannel(String channelName) {
		return channels.get(channelName.toLowerCase());
	}
	
	public IRCChannel getOrCreateChannel(String channelName) {
		IRCChannel channel = getChannel(channelName);
		if(channel == null) {
			channel = new IRCChannelImpl(this, channelName);
			channels.put(channelName.toLowerCase(), channel);
		}
		return channel;
	}
	
	@Override
	public IRCUser getUser(String nick) {
		return users.get(nick.toLowerCase());
	}
	
	@Override
	public IRCUser getOrCreateUser(String nick) {
		IRCUser user = getUser(nick);
		if(user == null) {
			user = new IRCUserImpl(this, nick);
			users.put(nick.toLowerCase(), user);
		}
		return user;
	}

	@Override
	public String getHost() {
		return host;
	}
	
	public Collection<IRCChannel> getChannels() {
		return channels.values();
	}
	
	public boolean start() {
		if(MinecraftForge.EVENT_BUS.post(new IRCConnectingEvent(this))) {
			return false;
		}
		thread = new Thread(this);
		thread.start();
		return true;
	}

	protected Proxy createProxy() {
		if(!NetworkConfig.proxyHost.isEmpty()) {
			if(!NetworkConfig.proxyUsername.isEmpty() || !NetworkConfig.proxyPassword.isEmpty()) {
				Authenticator.setDefault(new ProxyAuthenticator(NetworkConfig.proxyUsername, NetworkConfig.proxyPassword));
			}
			SocketAddress proxyAddr = new InetSocketAddress(Utils.extractHost(NetworkConfig.proxyHost), Utils.extractPort(NetworkConfig.proxyHost, DEFAULT_PROXY_PORT));
			return new Proxy(Proxy.Type.SOCKS, proxyAddr);
		}
		return null;
	}

	protected Socket connect() {
		try {
			SocketAddress targetAddr = new InetSocketAddress(host, port);
			Socket newSocket;
			Proxy proxy = createProxy();
			if(proxy != null) {
				newSocket = new Socket(proxy);
			} else {
				newSocket = new Socket();
			}
			newSocket.connect(targetAddr);
			writer = new BufferedWriter(new OutputStreamWriter(newSocket.getOutputStream(), charset));
			reader = new BufferedReader(new InputStreamReader(newSocket.getInputStream(), charset));
			sender.setWriter(writer);
			return newSocket;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void run() {
		try {
			socket = connect();
			if(socket == null) {
				MinecraftForge.EVENT_BUS.post(new IRCDisconnectEvent(this));
				return;
			}
			register();
			sender.start();
			String line;
			while((line = reader.readLine()) != null) {
				if(GlobalConfig.debugMode) {
					System.out.println(line);
				}
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
		if(connected) {
			tryReconnect();
		}
	}
	
	public void tryReconnect() {
		MinecraftForge.EVENT_BUS.post(new IRCDisconnectEvent(this));
		start();
	}
	
	@Override
	public void disconnect(String quitMessage) {
		try {
			connected = false;
			if(writer != null) {
				writer.write("QUIT :" + quitMessage + "\r\n");
				writer.flush();
			}
			if(socket != null) {
				socket.close();
			}
			MinecraftForge.EVENT_BUS.post(new IRCDisconnectEvent(this));
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
			if(connected) {
				tryReconnect();
			}
		}
	}
	
	@Override
	public void nick(String nick) {
		if(irc("NICK " + nick)) {
			this.nick = nick;
		}
	}
	
	@Override
	public void join(String channelName, String channelKey) {
		irc("JOIN " + channelName + (channelKey != null ? (" " + channelKey) : ""));
	}
	
	@Override
	public void part(String channelName) {
		if(irc("PART " + channelName)) {
			IRCChannel channel = getChannel(channelName);
			if(channel != null) {
				MinecraftForge.EVENT_BUS.post(new IRCChannelLeftEvent(this, channel));
			}
			channels.remove(channelName.toLowerCase());
		}
	}
	
	@Override
	public void mode(String targetName, String flags) {
		irc("MODE " + targetName + " " + flags);
	}
	
	@Override
	public void mode(String targetName, String flags, String nick) {
		irc("MODE " + targetName + " " + flags + " " + nick);
	}
	
	public void topic(String channelName) {
		irc("TOPIC " + channelName);
	}
	
	@Override
	public void topic(String channelName, String topic) {
		irc("TOPIC " + channelName + " :" + topic);
	}
	
	private boolean handleNumericMessage(IRCMessage msg) {
		int numeric = msg.getNumericCommand();
		if(numeric == -1) {
			return false;
		}
		if(numeric == IRCReplyCodes.RPL_NAMREPLY) {
			IRCChannelImpl channel = (IRCChannelImpl) getChannel(msg.arg(2));
			String[] names = msg.arg(3).split(" ");
			for (String name : names) {
				boolean isOp = false;
				boolean isVoice = false;
				if (name.startsWith("@")) {
					isOp = true;
				} else if (name.startsWith("+")) {
					isVoice = true;
				}
				if (isOp || isVoice) {
					name = name.substring(1);
				}
				IRCUserImpl user = (IRCUserImpl) getOrCreateUser(name);
				user.setVoice(channel, isVoice);
				user.setOperator(channel, isOp);
				user.addChannel(channel);
				channel.addUser(user);
			}
			MinecraftForge.EVENT_BUS.post(new IRCChannelJoinedEvent(this, channel));
		} else if(numeric == IRCReplyCodes.RPL_ENDOFMOTD) {
			connected = true;
			MinecraftForge.EVENT_BUS.post(new IRCConnectEvent(this));
		} else if(numeric == IRCReplyCodes.RPL_TOPIC) {
			IRCChannelImpl channel = (IRCChannelImpl) getChannel(msg.arg(1));
			if(channel != null) {
				channel.setTopic(msg.arg(2));
				MinecraftForge.EVENT_BUS.post(new IRCChannelTopicEvent(this, channel, null, channel.getTopic()));
			}
		} else if(numeric == IRCReplyCodes.RPL_WHOISLOGIN) {
			IRCUserImpl user = (IRCUserImpl) getOrCreateUser(msg.arg(1));
			user.setAuthLogin(msg.arg(2));
		} else if(numeric == IRCReplyCodes.RPL_IDENTIFIED) {
			IRCUserImpl user = (IRCUserImpl) getOrCreateUser(msg.arg(1));
			user.setAuthLogin(msg.arg(1));
		} else if(numeric == IRCReplyCodes.ERR_NICKNAMEINUSE || numeric == IRCReplyCodes.ERR_ERRONEUSNICKNAME) {
			MinecraftForge.EVENT_BUS.post(new IRCErrorEvent(this, msg.getNumericCommand(), msg.args()));
		} else if(numeric == IRCReplyCodes.RPL_MOTD) {
			// ignore
		} else if(numeric == IRCReplyCodes.ERR_PASSWDMISMATCH) {
			MinecraftForge.EVENT_BUS.post(new IRCErrorEvent(this, msg.getNumericCommand(), msg.args()));
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
			irc("PONG " + msg.arg(0));
		} else if(cmd.equals("PRIVMSG")) {
			IRCUserImpl user = (IRCUserImpl) getOrCreateUser(msg.getNick());
			String target = msg.arg(0);
			String message = msg.arg(1);
			boolean isEmote = false;
			if(message.startsWith(EMOTE_START)) {
				message = message.substring(EMOTE_START.length(), message.length() - EMOTE_END.length());
				isEmote = true;
			}
			if(target.startsWith("#")) {
				MinecraftForge.EVENT_BUS.post(new IRCChannelChatEvent(this, getChannel(target), user, message, isEmote));
			} else if(target.equals(this.nick)) {
				MinecraftForge.EVENT_BUS.post(new IRCPrivateChatEvent(this, user, message, isEmote));
			}
		} else if(cmd.equals("NOTICE")) {
			IRCUserImpl user = null;
			if(msg.getNick() != null) {
				user = (IRCUserImpl) getOrCreateUser(msg.getNick());
			}
			String target = msg.arg(0);
			String message = msg.arg(1);
			if(target.startsWith("#")) {
				MinecraftForge.EVENT_BUS.post(new IRCChannelChatEvent(this, getChannel(target), user, message, false, true));
			} else if(target.equals(this.nick)) {
				MinecraftForge.EVENT_BUS.post(new IRCPrivateChatEvent(this, user, message, false, true));
			}
		} else if(cmd.equals("JOIN")) {
			IRCUserImpl user = (IRCUserImpl) getOrCreateUser(msg.getNick());
			IRCChannelImpl channel = (IRCChannelImpl) getOrCreateChannel(msg.arg(0));
			channel.addUser(user);
			user.addChannel(channel);
			MinecraftForge.EVENT_BUS.post(new IRCUserJoinEvent(this, channel, user));
		} else if(cmd.equals("PART")) {
			IRCUserImpl user = (IRCUserImpl) getOrCreateUser(msg.getNick());
			IRCChannelImpl channel = (IRCChannelImpl) getChannel(msg.arg(0));
			if(channel != null) {
				channel.removeUser(user);
				user.removeChannel(channel);
				MinecraftForge.EVENT_BUS.post(new IRCUserLeaveEvent(this, channel, user, msg.arg(1)));
			}
		} else if(cmd.equals("TOPIC")) {
			IRCUser user = getOrCreateUser(msg.getNick());
			IRCChannelImpl channel = (IRCChannelImpl) getChannel(msg.arg(0));
			if(channel != null) {
				channel.setTopic(msg.arg(1));
				MinecraftForge.EVENT_BUS.post(new IRCChannelTopicEvent(this, channel, user, channel.getTopic()));
			}
		} else if(cmd.equals("NICK")) {
			String newNick = msg.arg(0);
			IRCUserImpl user = (IRCUserImpl) getOrCreateUser(msg.getNick());
			users.remove(user.getName().toLowerCase());
			String oldNick = user.getName();
			user.setName(newNick);
			users.put(user.getName().toLowerCase(), user);
			MinecraftForge.EVENT_BUS.post(new IRCUserNickChangeEvent(this, user, oldNick, newNick));
		} else if(cmd.equals("MODE")) {
			if(!msg.arg(0).startsWith("#")) {
				return false;
			}
			IRCChannelImpl channel = (IRCChannelImpl) getOrCreateChannel(msg.arg(0));
			String mode = msg.arg(1);
			String param = null;
			if(msg.argcount() > 2) {
				param = msg.arg(2);
			}
			boolean set = false;
			List<Character> setList = new ArrayList<Character>();
			List<Character> unsetList = new ArrayList<Character>();
			for(int i = 0; i < mode.length(); i++) {
				char c = mode.charAt(i);
				if(c == '+') {
					set = true;
				} else if(c == '-') {
					set = false;
				} else if(set) {
					setList.add(c);
				} else {
					unsetList.add(c);
				}
			}
			for (char c : setList) {
				if (c == 'o') {
					IRCUserImpl user = (IRCUserImpl) getOrCreateUser(param);
					user.setOperator(channel, true);
				} else if (c == 'v') {
					IRCUserImpl user = (IRCUserImpl) getOrCreateUser(param);
					user.setVoice(channel, true);
				}
			}
			for (char c : unsetList) {
				if (c == 'o') {
					IRCUserImpl user = (IRCUserImpl) getOrCreateUser(param);
					user.setOperator(channel, true);
				} else if (c == 'v') {
					IRCUserImpl user = (IRCUserImpl) getOrCreateUser(param);
					user.setVoice(channel, true);
				}
			}
		} else if(cmd.equals("QUIT")) {
			IRCUser user = getOrCreateUser(msg.getNick());
			MinecraftForge.EVENT_BUS.post(new IRCUserQuitEvent(this, user, msg.arg(0)));
			for(IRCChannel channel : user.getChannels()) {
				((IRCChannelImpl) channel).removeUser(user);
			}
			users.remove(user.getName().toLowerCase());
		}
		return false;
	}

	public void whois(String nick) {
		irc("WHOIS " + nick);
	}
	
	public void message(String target, String message) {
		irc("PRIVMSG " + target + " :" + message);
	}

	public void notice(String target, String message) {
		irc("NOTICE " + target + " :" + message);
	}
	
	@Override
	public void kick(String channelName, String nick, String reason) {
		irc("KICK " + channelName + " " + nick + (reason != null ? (" :" + reason) : ""));
	}
	
	@Override
	public boolean irc(String message) {
		return sender.addToSendQueue(message);
	}
	
	@Override
	public String getServerType() {
		return serverType;
	}

	@Override
	public IRCBot getBot() {
		return bot;
	}

	@Override
	public String getIdentifier() {
		return host + (port != DEFAULT_PORT ? ":" + port : "");
	}

	@Override
	public int getPort() {
		return port;
	}

	public boolean isConnected() {
		return connected;
	}

}
