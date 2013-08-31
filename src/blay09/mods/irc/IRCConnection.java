// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import blay09.mods.irc.config.ConfigurationHandler;
import blay09.mods.irc.config.GlobalConfig;
import blay09.mods.irc.config.Globals;
import blay09.mods.irc.config.NickServSettings;
import blay09.mods.irc.config.ServerConfig;

public class IRCConnection implements Runnable {

	private static final int IRC_PORT = 6667;
	private static final String LOGIN = "EiraIRC";
	private static final String DESCRIPTION = "EiraIRC Bot";
	
	private String host;
	private ServerConfig config;
	private boolean connected;
	private Map<String, List<String>> channelUserMap;
	private List<String> privateChannels;
	private int tickTimer;
	
	private Thread thread;
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	
	public IRCConnection(String host, boolean clientSide) {
		this.host = host;
		config = ConfigurationHandler.getServerConfig(host);
		config.clientSide = clientSide;
		channelUserMap = new HashMap<String, List<String>>();
		if(clientSide) {
			if(config.nick.isEmpty()) {
				config.nick = Minecraft.getMinecraft().thePlayer.username;
			}
			privateChannels = new ArrayList<String>();
		}
	}
	
	public boolean connect() {
		try {
			socket = new Socket(host, IRC_PORT);
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
	
	public List<String> getUserList(String channel) {
		List<String> list = channelUserMap.get(channel);
		if(list == null) {
			list = new ArrayList<String>();
			channelUserMap.put(channel, list);
		}
		return channelUserMap.get(channel);
	}
	
	public List<String> getPrivateChannels() {
		return privateChannels;
	}
	
	public void onUserJoin(String channel, String user) {
		String nick = Utils.getNickFromUser(user);
		EiraIRC.instance.getEventHandler().onIRCJoin(this, channel, user, nick);
		List<String> userList = channelUserMap.get(channel);
		if(userList == null) {
			userList = new ArrayList<String>();
			channelUserMap.put(channel, userList);
		}
		userList.add(nick);
	}
	
	public void nickServ() {
		NickServSettings settings = NickServSettings.settings.get(host);
		if(settings == null) {
			return;
		}
		if(!config.nickServName.isEmpty() && !config.nickServPassword.isEmpty()) {
			sendPrivateMessage(settings.getBotName(), settings.getCommand() + " " + config.nickServName + " " + config.nickServPassword);
		}
	}
	
	public void onNickChange(String user, String newNick) {
		String oldNick = Utils.getNickFromUser(user);
		for(List<String> list : channelUserMap.values()) {
			if(list.contains(oldNick)) {
				list.remove(oldNick);
				list.add(newNick);
			}
		}
		EiraIRC.instance.getEventHandler().onIRCNickChange(this, Utils.getNickFromUser(user), newNick);
	}
	
	public void onPrivateMessage(String user, String message) {
		String nick = Utils.getNickFromUser(user);
		if(nick.equals("jtv") && host.equals(Globals.TWITCH_SERVER)) {
			// ignore them for now, maybe transfer Twitch colors to MC colors at some point later
			return;
		}
		if(config.clientSide) {
			EiraIRC.instance.getEventHandler().onIRCPrivateMessage(this, user, Utils.getNickFromUser(user), message);
			return;
		}
		message = message.toUpperCase();
		if(message.equals("HELP")) {
			sendPrivateMessage(nick, "***** EiraIRC Help *****");
			sendPrivateMessage(nick, "EiraIRC connects a Minecraft client or a whole server");
			sendPrivateMessage(nick, "to one or multiple IRC channels and servers.");
			sendPrivateMessage(nick, "Visit <...> for more information on this bot.");
			sendPrivateMessage(nick, " ");
			sendPrivateMessage(nick, "The following commands are available:");
			sendPrivateMessage(nick, "WHO            Prints out a list of all players online");
			sendPrivateMessage(nick, "HELP            Prints this command list");
			sendPrivateMessage(nick, "ALIAS            Look up the username of an online player");
			sendPrivateMessage(nick, "MSG            Send a private message to an online player");
			sendPrivateMessage(nick, "***** End of Help *****");
			return;
		} else if(message.equals("WHO")) {
			List<EntityPlayerMP> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			sendPrivateMessage(nick, playerEntityList.size() + " players online:");
			String s = "* ";
			for(int i = 0; i < playerEntityList.size(); i++) {
				EntityPlayerMP entityPlayer = playerEntityList.get(i);
				String alias = Utils.getAliasForPlayer(entityPlayer);
				if(s.length() + alias.length() + 2 >= 100) {
					sendPrivateMessage(nick, s);
					s = "* ";
				}
				if(s.length() > 2) {
					s += ", ";
				}
				s += alias;
			}
			if(s.length() > 2) {
				sendPrivateMessage(nick, s);
			}
			return;
		} else if(message.startsWith("ALIAS ")) {
			if(!GlobalConfig.enableAliases) {
				sendPrivateMessage(nick, "Aliases are not enabled on this server.");
				return;
			}
			int i = message.indexOf(" ", 7);
			String alias = message.substring(7);
			List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for(EntityPlayer entity : playerEntityList) {
				if(Utils.getAliasForPlayer(entity).equals(alias)) {
					sendPrivateMessage(nick, "The username for '" + alias + "' is '" + entity.username + "'.");
					return;
				}
			}
			sendPrivateMessage(nick, "That player cannot be found.");
			return;
		} else if(message.startsWith("MSG ")) {
			if(!GlobalConfig.allowPrivateMessages || !config.allowPrivateMessages) {
				sendPrivateMessage(nick, "Private messages are disabled on this server.");
				return;
			}
			int i = message.indexOf(" ", 5);
			String playerName = message.substring(4, i);
			EntityPlayer entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
			if(entityPlayer == null) {
				List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
				for(EntityPlayer entity : playerEntityList) {
					if(Utils.getAliasForPlayer(entity).equals(playerName)) {
						entityPlayer = entity;
					}
				}
				if(entityPlayer == null) {
					sendPrivateMessage(nick, "That player cannot be found.");
					return;
				}
			}
			String targetMessage = message.substring(i + 1);
			EiraIRC.instance.getEventHandler().onIRCPrivateMessageToPlayer(this, user, nick, entityPlayer, targetMessage);
			sendPrivateMessage(nick, "Message sent to " + playerName + ": " + targetMessage);
			return;
		}
		sendPrivateMessage(nick, "Unknown command. Type HELP for a list of all commands.");
	}
	
	public void onPrivateEmote(String user, String message) {
		EiraIRC.instance.getEventHandler().onIRCPrivateEmote(this, user, Utils.getNickFromUser(user), message);
	}
	
	public void onChannelEmote(String channel, String user, String message) {
		EiraIRC.instance.getEventHandler().onIRCEmote(this, channel, user, Utils.getNickFromUser(user), message);
	}
	
	public void onChannelMessage(String channel, String user, String message) {
		EiraIRC.instance.getEventHandler().onIRCMessage(this, channel, user, Utils.getNickFromUser(user), message);			
	}
	
	public void onUserPart(String channel, String user) {
        String nick = Utils.getNickFromUser(user);
		EiraIRC.instance.getEventHandler().onIRCPart(this, channel, user, nick);
        List<String> userList = channelUserMap.get(channel);
		if(userList == null) {
			userList = new ArrayList<String>();
			userList = channelUserMap.put(channel, userList);
		}
		userList.remove(nick);
	}
	
	@Override
	public void run() {
		try {
			if(!config.serverPassword.isEmpty()) {
				writer.write("PASS " + config.serverPassword + "\r\n");
				writer.flush();
			}
			changeNick(config.nick.isEmpty() ? GlobalConfig.nick + (int) (Math.random() * 10000) : config.nick);
			writer.write("USER " + LOGIN + " \"\" \"\" :" + DESCRIPTION + "\r\n");
			writer.flush();
			String line = null;
			while((line = reader.readLine()) != null) {
				// Ping
				if(line.startsWith("PING ")) {
					writer.write("PONG " + line.substring(5) + "\r\n");
					writer.flush();
					continue;
				}
				// Message
				if(line.contains(" PRIVMSG ")) {
					int i = line.indexOf(" PRIVMSG ");
					String user = line.substring(1, i);
					int j = line.indexOf(":", i);
					String channel = line.substring(i + 9, j - 1);
					String message = null;
					if(line.contains("ACTION")) {
						message = line.substring(j + 9, line.length() - 1);
						if(channel.equals(config.nick)) {
							onPrivateEmote(user, message);
						} else {
							onChannelEmote(channel, user, message);
						}
					} else {
						message = line.substring(j + 1);
						if(channel.equals(config.nick)) {
							onPrivateMessage(user, message);
						} else {
							onChannelMessage(channel, user, message);
						}
					}
					continue;
				}
				// User Part
				if(line.contains(" PART ")) {
					int i = line.indexOf(" PART ");
					String user = line.substring(1, i);
					String channel = line.substring(i + 6);
					onUserPart(channel, user);
				}
				// User Join
				if(line.contains(" JOIN ")) {
					int i = line.indexOf(" JOIN ");
					String user = line.substring(1, i);
					String channel = line.substring(i + 6);
					onUserJoin(channel, user);
				}
				// Nick Change Success
				if(line.contains(" NICK ")) {
					int i = line.indexOf(" NICK ");
					String user = line.substring(1, i);
					String nick = line.substring(i + 7);
					onNickChange(user, nick);
				}
				// Nick Already in Use
				if(line.contains(" 433 ")) {
					changeNick(config.nick + "_");
					continue;
				}
				// Names List
				if(line.contains(" 353 ")) {
					int i = line.indexOf(" = ");
					int j = line.indexOf(":", i);
					String channel = line.substring(i + 3, j - 1);
					List<String> userList = channelUserMap.get(channel);
					if(userList == null) {
						userList = new ArrayList<String>();
						channelUserMap.put(channel, userList);
					} else {
						userList.clear();
					}
					String[] userArray = line.substring(j + 1).split(" ");
					for(int k = 0; k < userArray.length; k++) {
						if(userArray[k].startsWith("+") || userArray[k].startsWith("@")) {
							userArray[k] = userArray[k].substring(1);
						}
						userList.add(userArray[k]);
					}
					continue;
				}
				// End of MOTD
				if(line.contains(" 376 ")) {
					EiraIRC.instance.getEventHandler().onIRCConnect(this);
					nickServ();
					connected = true;
					for(String channel : config.channels) {
						joinChannel(channel);
					}
					continue;
				}
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		EiraIRC.instance.getEventHandler().onIRCDisconnect(this);
		EiraIRC.instance.removeConnection(this);
		if(connected) {
			if(connect()) {
				EiraIRC.instance.addConnection(this);
			}
		}
	}
	
	public void joinChannel(String channel) throws IOException {
		writer.write("JOIN " + channel + "\r\n");
		writer.flush();
		if(!config.channels.contains(channel)) {
			config.channels.add(channel);
			ConfigurationHandler.save();
		}
	}
	
	public void changeNick(String nick) throws IOException {
		writer.write("NICK " + nick + "\r\n");
		writer.flush();
		config.nick = nick;
		ConfigurationHandler.save();
	}

	public void sendChannelMessage(String channel, String message) {
		try {
			writer.write("PRIVMSG " + channel + " :" + message + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPrivateMessage(String nick, String message) {
		try {
			writer.write("PRIVMSG " + nick + " :" + message + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(config.clientSide && !privateChannels.contains(nick)) {
			NickServSettings settings = NickServSettings.settings.get(host);
			if(settings == null) {
				return;
			}
			if(!nick.equals(settings.getBotName())) {
				privateChannels.add(nick);
			}
		}
	}

	public String getHost() {
		return host;
	}

	public void broadcastMessage(String message, String requiredFlags) {
		for(String channel : config.channels) {
			if(config.hasChannelFlags(channel, requiredFlags)) {
				sendChannelMessage(channel, message);				
			}
		}
	}

	public void disconnect() {
		try {
			connected = false;
			if(socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void leaveChannel(String channel) throws IOException {
		writer.write("PART " + channel + "\r\n");
		writer.flush();
		config.channels.remove(channel);
		ConfigurationHandler.save();
	}

	public ServerConfig getConfig() {
		return config;
	}

}
