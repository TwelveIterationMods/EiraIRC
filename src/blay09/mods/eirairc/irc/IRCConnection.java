package blay09.mods.eirairc.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.IRCEventHandler;
import blay09.mods.eirairc.Utils;

public class IRCConnection implements Runnable {

	public static final int IRC_DEFAULT_PORT = 6667;
	private static final String DEFAULT_LOGIN = "EiraIRC";
	private static final String DEFAULT_DESCRIPTION = "EiraIRC Bot";
	
	private final int port;
	private final String host;
	private final String password;
	private String nick;
	private String login;
	private String description;
	private boolean connected;
	private IIRCEventHandler eventHandler;
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
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public void setDescription(String description) {
		this.description = description;
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
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
				System.out.println(line);
				if(handlePing(line, cmd)) {
					continue;
				}
				if(handleNumericReply(line, cmd)) {
					continue;
				}
				if(handleMessage(line, cmd)) {
					continue;
				}
				// Message
//				// Nick Already in Use
//				if(line.contains(" 433 ")) {
//					changeNick(config.nick + "_");
//					continue;
//				}
//				// Names List
//				if(line.contains(" 353 ")) {
//					int i = line.indexOf(" = ");
//					int j = line.indexOf(":", i);
//					String channel = line.substring(i + 3, j - 1);
//					List<String> userList = channelUserMap.get(channel);
//					if(userList == null) {
//						userList = new ArrayList<String>();
//						channelUserMap.put(channel, userList);
//					} else {
//						userList.clear();
//					}
//					String[] userArray = line.substring(j + 1).split(" ");
//					for(int k = 0; k < userArray.length; k++) {
//						if(userArray[k].startsWith("+") || userArray[k].startsWith("@")) {
//							userArray[k] = userArray[k].substring(1);
//						}
//						userList.add(userArray[k]);
//					}
//					continue;
//				}
//				// End of MOTD
//				if(line.contains(" 376 ")) {
//					EiraIRC.instance.getEventHandler().onIRCConnect(this);
//					nickServ();
//					connected = true;
//					for(String channel : config.channels) {
//						joinChannel(channel, config.channelPasswords.get(channel));
//					}
//					continue;
//				}
//				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		EiraIRC.instance.getEventHandler().onIRCDisconnect(this);
//		EiraIRC.instance.removeConnection(this);
//		if(connected) {
//			if(connect()) {
//				EiraIRC.instance.addConnection(this);
//			}
//		}
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
		}
	}
	
	public void nick(String nick) {
		try {
			writer.write("NICK " + nick + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void join(String channelName, String channelKey) {
		try {
			writer.write("JOIN " + channelName + " " + channelKey + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void part(String channelName) {
		try {
			writer.write("PART " + channelName + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mode(String targetName, String flags) {
		mode(targetName, flags, null);
	}
	
	public void mode(String targetName, String flags, String nick) {
		try {
			writer.write("MODE " + targetName + " " + flags + (nick != null ? " " + nick : "") + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void topic(String channelName) {
		topic(channelName, null);
	}
	
	public void topic(String channelName, String topic) {
		try {
			writer.write("TOPIC " + channelName + (topic != null ? " :" + topic : "") + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		switch(replyCode) {
		case IRCReplyCodes.RPL_NAMREPLY:
//			cmd[3] = cmd[3].substring(1);
//			for(int i = 3; i < cmd.length; i++) {
//				
//			}
//			int i = line.indexOf(" = ");
//			int j = line.indexOf(":", i);
//			String channel = line.substring(i + 3, j - 1);
//			List<String> userList = channelUserMap.get(channel);
//			if(userList == null) {
//				userList = new ArrayList<String>();
//				channelUserMap.put(channel, userList);
//			} else {
//				userList.clear();
//			}
//			String[] userArray = line.substring(j + 1).split(" ");
//			for(int k = 0; k < userArray.length; k++) {
//				if(userArray[k].startsWith("+") || userArray[k].startsWith("@")) {
//					userArray[k] = userArray[k].substring(1);
//				}
//				userList.add(userArray[k]);
//			}
			break;
		case IRCReplyCodes.RPL_ENDOFMOTD:
			connected = true;
			eventHandler.onConnected(this);
			break;
		}
		return false;
	}
	
	private boolean handleMessage(String line, String[] cmd) {
		String nick = Utils.getNickFromUser(cmd[0].substring(1));
		String msg = cmd[1];
		if(msg.equals("PRIVMSG")) {
			IRCUser user = users.get(nick);
			if(user == null) {
				user = new IRCUser(this, nick);
				users.put(nick, user);
			}
			String target = cmd[2];
			int messageIdx = cmd[0].length() + 9 + target.length() + 2;
			String message = line.substring(messageIdx);
			boolean isEmote = false;
			if(message.contains("ACTION")) {
				message = line.substring(messageIdx + 8, line.length() - 1);
				isEmote = true;
			}
			if(target.startsWith("#")) {
				IRCChannel channel = channels.get(target);
				if(isEmote) {
					eventHandler.onChannelEmote(this, channel, user, message);
				} else {
					eventHandler.onChannelMessage(this, channel, user, message);
				}
			} else if(target.equals(nick)) {
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
			IRCChannel channel = channels.get(cmd[2]);
			if(channel == null) {
				channel = new IRCChannel(this, cmd[2]);
				channels.put(cmd[2], channel);
			}
			channel.addUser(user);
			eventHandler.onUserJoin(this, user, channel);
		} else if(msg.equals("PART")) {
			IRCUser user = users.get(nick);
			if(user == null) {
				user = new IRCUser(this, nick);
				users.put(nick, user);
			}
			IRCChannel channel = channels.get(cmd[2]);
			channel.removeUser(user);
			int quitMessageIdx = cmd[0].length() + 6 + cmd[2].length() + 2;
			String quitMessage = null;
			if(line.length() >= quitMessageIdx) {
				quitMessage = line.substring(quitMessageIdx);
			}
			eventHandler.onUserPart(this, user, channel, quitMessage);
		} else if(msg.equals("NICK")) {
			String newNick = cmd[2].substring(1);
			IRCUser user = users.get(nick);
			if(user == null) {
				user = new IRCUser(this, nick);
			}
			eventHandler.onNickChange(this, user, newNick);
			users.remove(user.getNick());
			user.setNick(newNick);
			users.put(user.getNick(), user);
		} else if(msg.equals("QUIT")) {
			int quitMessageIdx = cmd[0].length() + 8;
			String quitMessage = null;
			if(line.length() >= quitMessageIdx) {
				quitMessage = line.substring(cmd[0].length() + 6);
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

	public void sendPrivateMessage(IRCUser user, String message) {
		sendPrivateMessage(user.getNick(), message);
	}
	
	public void sendPrivateMessage(String nick, String message) {
		try {
			writer.write("PRIVMSG " + nick + " :" + message + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendChannelMessage(IRCChannel channel, String message) {
		try {
			writer.write("PRIVMSG " + channel.getName() + " :" + message + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
