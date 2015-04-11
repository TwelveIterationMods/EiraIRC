// Copyright (c) 2014, Christopher "blay09" Baker

package net.blay09.mods.eirairc.irc;

import net.blay09.mods.eirairc.api.IRCReplyCodes;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.event.*;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.MinecraftForge;

import java.io.*;
import java.net.*;
import java.util.*;

public class IRCConnectionImpl implements Runnable, IRCConnection {

	public static class ProxyAuthenticator extends Authenticator {
		private final PasswordAuthentication auth;

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

	protected final IRCSender sender = new IRCSender(this);
	private final Map<String, IRCChannel> channels = new HashMap<String, IRCChannel>();
	private final Map<String, IRCUser> users = new HashMap<String, IRCUser>();
	protected final ServerConfig serverConfig;

	protected final int[] ports;
	protected final String host;
	private IRCBotImpl bot;
	private String nick;
	private boolean connected;
	private int waitingReconnect;

	private String serverType;
	private String channelTypes = "#&";
	private String channelUserModes = "ov";
	private String channelUserModePrefixes = "@+";

	private Socket socket;
	protected BufferedWriter writer;
	protected BufferedReader reader;
	public IRCConnectionImpl(ServerConfig serverConfig, String nick) {
		this.serverConfig = serverConfig;
		this.host = Utils.extractHost(serverConfig.getAddress());
		this.ports = Utils.extractPorts(serverConfig.getAddress(), DEFAULT_PORT);
		this.nick = nick;
	}

	public void setBot(IRCBotImpl bot) {
		this.bot = bot;
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

	@Override
	public Collection<IRCChannel> getChannels() {
		return channels.values();
	}

	public boolean start() {
		if(MinecraftForge.EVENT_BUS.post(new IRCConnectingEvent(this))) {
			return false;
		}
		Thread thread = new Thread(this, "IRC (" + host + ")");
		thread.start();
		return true;
	}

	protected Proxy createProxy() {
		if(!SharedGlobalConfig.proxyHost.isEmpty()) {
			if(!SharedGlobalConfig.proxyUsername.isEmpty() || !SharedGlobalConfig.proxyPassword.isEmpty()) {
				Authenticator.setDefault(new ProxyAuthenticator(SharedGlobalConfig.proxyUsername, SharedGlobalConfig.proxyPassword));
			}
			SocketAddress proxyAddr = new InetSocketAddress(Utils.extractHost(SharedGlobalConfig.proxyHost), Utils.extractPorts(SharedGlobalConfig.proxyHost, DEFAULT_PROXY_PORT)[0]);
			return new Proxy(Proxy.Type.SOCKS, proxyAddr);
		}
		return null;
	}

	protected Socket connect() throws Exception {
		for(int i = 0; i < ports.length; i++) {
			try {
				SocketAddress targetAddr = new InetSocketAddress(host, ports[i]);
				Socket newSocket;
				Proxy proxy = createProxy();
				if (proxy != null) {
					newSocket = new Socket(proxy);
				} else {
					newSocket = new Socket();
				}

				if (!SharedGlobalConfig.bindIP.isEmpty()) {
					newSocket.bind(new InetSocketAddress(SharedGlobalConfig.bindIP, ports[i]));
				}
				newSocket.connect(targetAddr);
				writer = new BufferedWriter(new OutputStreamWriter(newSocket.getOutputStream(), serverConfig.getCharset()));
				reader = new BufferedReader(new InputStreamReader(newSocket.getInputStream(), serverConfig.getCharset()));
				sender.setWriter(writer);
				return newSocket;
			} catch (UnknownHostException e) {
				throw e;
			} catch (IOException e) {
				if(i == ports.length - 1) {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public void run() {
		try {
			try {
				socket = connect();
			} catch (Exception e) {
				MinecraftForge.EVENT_BUS.post(new IRCConnectionFailedEvent(this, e));
				return;
			}
			register();
			sender.start();
			String line;
			while((line = reader.readLine()) != null) {
				if(SharedGlobalConfig.debugMode) {
					System.out.println(line);
				}
				if(!line.isEmpty()) {
					IRCMessage msg = parser.parse(line);
					if(!handleNumericMessage(msg)) {
						handleMessage(msg);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		MinecraftForge.EVENT_BUS.post(new IRCDisconnectEvent(this));
		if(connected) {
			tryReconnect();
		}
	}

	public void tryReconnect() {
		if(waitingReconnect == 0) {
			waitingReconnect = 15000;
		} else {
			waitingReconnect *= 2;
		}
		MinecraftForge.EVENT_BUS.post(new IRCReconnectEvent(this, waitingReconnect));
		try {
			Thread.sleep(waitingReconnect);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void register() {
		try {
			if(serverConfig.getServerPassword() != null && !serverConfig.getServerPassword().isEmpty()) {
				writer.write("PASS " + serverConfig.getServerPassword() + "\r\n");
			}
			writer.write("NICK " + nick + "\r\n");
			writer.write("USER " + serverConfig.getBotSettings().getString(BotStringComponent.Ident) + " \"\" \"\" :" + serverConfig.getBotSettings().getString(BotStringComponent.Description) + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			MinecraftForge.EVENT_BUS.post(new IRCConnectionFailedEvent(this, e));
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
			IRCChannel channel = channels.remove(channelName.toLowerCase());
			if(channel != null) {
				MinecraftForge.EVENT_BUS.post(new IRCChannelLeftEvent(this, channel));
			}
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
				char firstChar = name.charAt(0);
				int idx = channelUserModePrefixes.indexOf(firstChar);
				IRCChannelUserMode mode = null;
				if(idx != -1) {
					mode = IRCChannelUserMode.fromChar(channelUserModes.charAt(idx));
					name = name.substring(1);
				}
				IRCUserImpl user = (IRCUserImpl) getOrCreateUser(name);
				if(mode != null) {
					user.setChannelUserMode(channel, mode);
				}
				user.addChannel(channel);
				channel.addUser(user);
			}
			MinecraftForge.EVENT_BUS.post(new IRCChannelJoinedEvent(this, channel));
		} else if(numeric == IRCReplyCodes.RPL_WELCOME) {
			connected = true;
			waitingReconnect = 0;
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
		} else if(numeric == IRCReplyCodes.RPL_ENDOFWHOIS) {
			IRCUserImpl user = (IRCUserImpl) getOrCreateUser(msg.arg(1));
			if(user.getAuthLogin() == null || user.getAuthLogin().isEmpty()) {
				user.setAuthLogin(null);
			}
		} else if(numeric == IRCReplyCodes.ERR_NICKNAMEINUSE || numeric == IRCReplyCodes.ERR_ERRONEUSNICKNAME) {
			MinecraftForge.EVENT_BUS.post(new IRCErrorEvent(this, msg.getNumericCommand(), msg.args()));
		} else if(numeric == IRCReplyCodes.ERR_PASSWDMISMATCH) {
			MinecraftForge.EVENT_BUS.post(new IRCErrorEvent(this, msg.getNumericCommand(), msg.args()));
		} else if(numeric == IRCReplyCodes.RPL_ISUPPORT) {
			for(int i = 0; i < msg.argcount(); i++) {
				if(msg.arg(i).startsWith("CHANTYPES=")) {
					channelTypes = msg.arg(i).substring(10);
				} else if(msg.arg(i).startsWith("PREFIX=")) {
					String value = msg.arg(i).substring(7);
					StringBuilder sb = new StringBuilder();
					for(int j = 0; j < value.length(); j++) {
						char c = value.charAt(j);
						if(c == ')') {
							channelUserModes = sb.toString();
							sb = new StringBuilder();
						} else if(c != '(') {
							sb.append(c);
						}
					}
					channelUserModePrefixes = sb.toString();
				}
			}
		} else if(numeric == IRCReplyCodes.RPL_MOTD || numeric <= 4 || numeric == 251 || numeric == 252 || numeric == 254 || numeric == 255 || numeric == 265 || numeric == 266 || numeric == 250 || numeric == 375) {
			if(SharedGlobalConfig.debugMode) {
				System.out.println("Ignored message code: " + msg.getCommand() + " (" + msg.argcount() + " arguments)");
			}
		} else {
			if(SharedGlobalConfig.debugMode) {
				System.out.println("Unhandled message code: " + msg.getCommand() + " (" + msg.argcount() + " arguments)");
			}
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
			if(channelTypes.indexOf(target.charAt(0)) != -1) {
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
			if(channelTypes.indexOf(target.charAt(0)) != -1) {
				MinecraftForge.EVENT_BUS.post(new IRCChannelChatEvent(this, getChannel(target), user, message, false, true));
			} else if(target.equals(this.nick) || target.equals("*")) {
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
			if(channelTypes.indexOf(msg.arg(0).charAt(0)) != -1 || msg.argcount() < 3) {
				return false;
			}
			IRCChannelImpl channel = (IRCChannelImpl) getOrCreateChannel(msg.arg(0));
			String mode = msg.arg(1);
			String param = msg.arg(2);
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
			IRCUserImpl user = (IRCUserImpl) getOrCreateUser(param);
			IRCChannelUserMode currentMode = user.getChannelUserMode(channel);
			for(char c : setList) {
				int idx = channelUserModes.indexOf(c);
				if(idx != -1) {
					user.setChannelUserMode(channel, IRCChannelUserMode.fromChar(c));
				}
			}
			for(char c : unsetList) {
				if(c == currentMode.modeChar) {
					user.setChannelUserMode(channel, null);
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
	public String getChannelTypes() {
		return channelTypes;
	}

	@Override
	public String getChannelUserModes() {
		return channelUserModes;
	}

	@Override
	public String getChannelUserModePrefixes() {
		return channelUserModePrefixes;
	}

	@Override
	public IRCBot getBot() {
		return bot;
	}

	@Override
	public String getName() {
		return host;
	}

	@Override
	public ContextType getContextType() {
		return ContextType.IRCConnection;
	}

	@Override
	public String getIdentifier() {
		return host;
	}

	@Override
	public IRCConnection getConnection() {
		return this;
	}

	@Override
	public void message(String message) {}

	@Override
	public void notice(String message) {}

	@Override
	public int[] getPorts() {
		return ports;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public boolean isConnected() {
		return connected;
	}

}
