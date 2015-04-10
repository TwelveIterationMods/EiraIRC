package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.GeneralBooleanComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;

import java.util.*;


public class ConnectionManager {

	private final Map<String, IRCConnection> connections = new HashMap<String, IRCConnection>();

	private boolean ircRunning;

	public void startIRC() {
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			if(serverConfig.getGeneralSettings().getBoolean(GeneralBooleanComponent.AutoJoin) && !serverConfig.isRedirect()) {
				Utils.connectTo(serverConfig);
			}
		}
		ircRunning = true;
	}

	public void stopIRC() {
		List<IRCConnection> dcList = new ArrayList<IRCConnection>();
		for(IRCConnection connection : connections.values()) {
			dcList.add(connection);
		}
		for(int i = 0; i < dcList.size(); i++) {
			dcList.get(i).disconnect(ConfigHelper.getQuitMessage(dcList.get(i)));
		}
		connections.clear();
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
}
