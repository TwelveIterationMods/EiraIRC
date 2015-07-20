// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc;

import com.google.common.collect.Lists;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.GeneralBooleanComponent;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.irc.ssl.IRCConnectionSSLImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.util.ChatComponentText;

import java.util.*;

public class ConnectionManager {

	private final Map<String, IRCConnection> connections = new HashMap<String, IRCConnection>();

	private boolean ircRunning;

	public void startIRC() {
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
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			if(serverConfig.getGeneralSettings().getBoolean(GeneralBooleanComponent.AutoJoin) && !serverConfig.isRedirect()) {
				connectTo(serverConfig);
			}
		}
		ircRunning = true;
	}

	public void stopIRC() {
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

	public boolean isIRCRunning() {
		return ircRunning;
	}

	public Collection<IRCConnection> getConnections() {
		return connections.values();
	}

	public void addConnection(IRCConnection connection) {
		connections.put(connection.getIdentifier(), connection);
	}

	public int getConnectionCount() {
		return connections.size();
	}

	public IRCConnection getDefaultConnection() {
		Iterator<IRCConnection> it = connections.values().iterator();
		if(it.hasNext()) {
			return it.next();
		}
		return null;
	}

	public IRCConnection getConnection(String identifier) {
		return connections.get(identifier);
	}

	public void removeConnection(IRCConnection connection) {
		connections.remove(connection.getHost());
	}

	public boolean isConnectedTo(String identifier) {
		return connections.containsKey(identifier);
	}

	public void clearConnections() {
		connections.clear();
	}

	public boolean isLatestConnection(IRCConnection connection) {
		IRCConnection latestConnection = connections.get(connection.getIdentifier());
		return latestConnection == null || latestConnection == connection;
	}

	public static boolean redirectTo(ServerConfig serverConfig, boolean solo) {
		if(serverConfig == null) {
			EiraIRC.instance.getConnectionManager().stopIRC();
			return true;
		}
		IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
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
		IRCConnection oldConnection = EiraIRC.instance.getConnectionManager().getConnection(config.getIdentifier());
		if(oldConnection != null) {
			oldConnection.disconnect("Reconnecting...");
		}
		IRCConnectionImpl connection;
		if(config.isSSL()) {
			connection = new IRCConnectionSSLImpl(config, ConfigHelper.getFormattedNick(config));
		} else {
			connection = new IRCConnectionImpl(config, ConfigHelper.getFormattedNick(config));
		}
		connection.setBot(new IRCBotImpl(connection));
		if(connection.start()) {
			return connection;
		}
		return null;
	}
}
