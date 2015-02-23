package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.api.InternalMethods;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.upload.UploadHoster;
import net.blay09.mods.eirairc.client.UploadManager;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.IRCTargetError;

/**
 * Created by Blay09 on 23.02.2015.
 */
public class InternalMethodsImpl implements InternalMethods {

	@Override
	public void registerSubCommand(SubCommand command) {
		IRCCommandHandler.registerCommand(command);
	}

	@Override
	public void registerUploadHoster(UploadHoster uploadHoster) {
		UploadManager.registerUploadHoster(uploadHoster);
	}

	@Override
	public boolean isConnectedTo(String serverHost) {
		return EiraIRC.instance.getConnectionManager().isConnectedTo(serverHost);
	}

	@Override
	public IRCContext parseContext(String contextPath) {
		String server;
		int serverIdx = contextPath.indexOf('/');
		IRCConnection connection;
		if(serverIdx != -1) {
			server = contextPath.substring(0, serverIdx);
			contextPath = contextPath.substring(serverIdx + 1);
			connection = EiraIRC.instance.getConnectionManager().getConnection(server);
			if(connection == null) {
				return IRCTargetError.NotConnected;
			}
		} else {
			IRCConnection foundConnection = null;
			for(IRCConnection con : EiraIRC.instance.getConnectionManager().getConnections()) {
				if(con.getChannel(contextPath) != null || con.getUser(contextPath) != null) {
					if(foundConnection != null) {
						return IRCTargetError.SpecifyServer;
					}
					foundConnection = con;
				}
			}
			if(foundConnection == null) {
				return IRCTargetError.ServerNotFound;
			}
			connection = foundConnection;
		}
		if(connection.getChannelTypes().indexOf(contextPath.charAt(0)) != -1) {
			IRCChannel channel = connection.getChannel(contextPath);
			if(channel == null) {
				return IRCTargetError.NotOnChannel;
			}
			return channel;
		} else {
			return connection.getOrCreateUser(contextPath);
		}
	}

}
