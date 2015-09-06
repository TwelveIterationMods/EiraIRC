// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc;

import com.google.common.collect.Lists;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.irc.ssl.IRCConnectionSSLImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.util.ChatComponentText;

import java.util.*;

public class ConnectionManager {

	private static final Map<String, IRCConnection> connections = new HashMap<>();

	private static boolean ircRunning;

	public static void startIRC() {
		if(!ConfigurationHandler.failedToLoad.isEmpty()) {
			StringBuilder sb = new StringBuilder("Failed to load EiraIRC configurations due to syntax errors: ");
			for(String s : ConfigurationHandler.failedToLoad) {
				if(sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(s);
			}
			Utils.addMessageToChat(new ChatComponentText(sb.toString()));
			Utils.addMessageToChat(new ChatComponentText("See the log for more information."));
		}
		if(!LocalConfig.disableModpackIRC.get()) {
			for (ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				if (serverConfig.getGeneralSettings().autoJoin.get() && !serverConfig.isRedirect()) {
					connectTo(serverConfig);
				}
			}
		}
		ircRunning = true;
	}

	public static void stopIRC() {
		List<IRCConnection> dcList = Lists.newArrayList();
		for(IRCConnection connection : connections.values()) {
			dcList.add(connection);
		}
		for(int i = 0; i < dcList.size(); i++) {
			dcList.get(i).disconnect(ConfigHelper.getQuitMessage(dcList.get(i)));
		}
		connections.clear();
		EiraIRC.instance.getChatSessionHandler().clear();
		ircRunning = false;
	}

	public static boolean isIRCRunning() {
		return ircRunning;
	}

	public static Collection<IRCConnection> getConnections() {
		return connections.values();
	}

	public static void addConnection(IRCConnection connection) {
		connections.put(connection.getIdentifier(), connection);
	}

	public static int getConnectionCount() {
		return connections.size();
	}

	public static IRCConnection getDefaultConnection() {
		Iterator<IRCConnection> it = connections.values().iterator();
		if(it.hasNext()) {
			return it.next();
		}
		return null;
	}

	public static IRCConnection getConnection(String identifier) {
		return connections.get(identifier);
	}

	public static void removeConnection(IRCConnection connection) {
		connections.remove(connection.getHost());
	}

	public static boolean isConnectedTo(String identifier) {
		return connections.containsKey(identifier);
	}

	public static void clearConnections() {
		connections.clear();
	}

	public static boolean isLatestConnection(IRCConnection connection) {
		IRCConnection latestConnection = connections.get(connection.getIdentifier());
		return latestConnection == null || latestConnection == connection;
	}

	public static boolean redirectTo(ServerConfig serverConfig, boolean solo) {
		if(serverConfig == null) {
			stopIRC();
			return true;
		}
		IRCConnection connection = getConnection(serverConfig.getIdentifier());
		if(connection != null && solo) {
			connection.disconnect("Redirected by server");
			connection = null;
		}
		if(connection == null) {
			connection = connectTo(serverConfig);
			if(connection == null) {
				return false;
			}
		} else {
			for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
				connection.join(channelConfig.getName(), AuthManager.getChannelPassword(channelConfig.getIdentifier()));
			}
		}
		return true;
	}

	public static IRCConnectionImpl connectTo(ServerConfig config) {
		IRCConnection oldConnection = getConnection(config.getIdentifier());
		if(oldConnection != null) {
			oldConnection.disconnect("Reconnecting...");
		}
		IRCConnectionImpl connection;
		if(config.isSSL()) {
			connection = new IRCConnectionSSLImpl(config, ConfigHelper.formatNick(config.getNick()));
		} else {
			connection = new IRCConnectionImpl(config, ConfigHelper.formatNick(config.getNick()));
		}
		connection.setBot(new IRCBotImpl(connection));
		if(connection.start()) {
			return connection;
		}
		return null;
	}

	public static void tickConnections() {
		for(IRCConnection connection : connections.values()) {
			((IRCConnectionImpl) connection).tick();
		}
	}
}
