package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.api.InternalMethods;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.api.upload.UploadHoster;
import net.blay09.mods.eirairc.client.UploadManager;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.util.IRCTargetError;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;


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
	public boolean hasClientSideInstalled(ICommandSender user) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(user.getCommandSenderName());
		return playerInfo.modInstalled;
	}

	@Override
	public boolean isConnectedTo(String serverHost) {
		return EiraIRC.instance.getConnectionManager().isConnectedTo(serverHost);
	}

	@Override
	public IRCContext parseContext(IRCContext parentContext, String contextPath, IRCContext.ContextType expectedType) {
		String server;
		int serverIdx = contextPath.indexOf('/');
		IRCConnection connection;
		if(serverIdx != -1) {
			server = contextPath.substring(0, serverIdx);
			contextPath = contextPath.substring(serverIdx + 1);
			connection = EiraIRC.instance.getConnectionManager().getConnection(server);
			if(connection == null) {
				return IRCTargetError.NotConnected;
			} else if(expectedType == IRCContext.ContextType.IRCConnection) {
				return connection;
			}
		} else {
			if (parentContext != null) {
				connection = parentContext.getConnection();
			} else {
				connection = EiraIRC.instance.getConnectionManager().getConnection(contextPath);
				if(connection != null) {
					if(expectedType == IRCContext.ContextType.IRCConnection) {
						return connection;
					} else {
						return IRCTargetError.InvalidTarget;
					}
				}
				IRCConnection foundConnection = null;
				for(IRCConnection con : EiraIRC.instance.getConnectionManager().getConnections()) {
					if(con.getChannel(contextPath) != null || con.getUser(contextPath) != null) {
						if (foundConnection != null) {
							return IRCTargetError.SpecifyServer;
						}
						foundConnection = con;
					}
				}
				if (foundConnection == null) {
					foundConnection = EiraIRC.instance.getConnectionManager().getConnectionCount() == 1 ? EiraIRC.instance.getConnectionManager().getDefaultConnection() : null;
					if(foundConnection == null) {
						return IRCTargetError.ServerNotFound;
					}
				}
				connection = foundConnection;
			}
		}
		if(expectedType == IRCContext.ContextType.IRCConnection) {
			return connection;
		}
		if(connection.getChannelTypes().indexOf(contextPath.charAt(0)) != -1) {
			if(expectedType != null && expectedType != IRCContext.ContextType.IRCChannel) {
				return IRCTargetError.InvalidTarget;
			}
			IRCChannel channel = connection.getChannel(contextPath);
			if(channel == null) {
				return IRCTargetError.NotOnChannel;
			}
			return channel;
		} else {
			if(expectedType != null && expectedType != IRCContext.ContextType.IRCUser) {
				return IRCTargetError.InvalidTarget;
			}
			if((parentContext != null && parentContext.getContextType() == IRCContext.ContextType.IRCChannel) || Utils.isServerSide()) {
				IRCUser user = connection.getUser(contextPath);
				if(user == null) {
					return IRCTargetError.UserNotFound;
				}
				return user;
			} else {
				return connection.getOrCreateUser(contextPath);
			}
		}
	}

	@Override
	public void relayChat(ICommandSender sender, String message, boolean isEmote, boolean isNotice, IRCContext target) {
		if(Utils.isServerSide()) {
			EiraIRC.instance.getMCEventHandler().relayChatServer(sender, message, isEmote, isEmote, target);
		} else {
			EiraIRC.instance.getMCEventHandler().relayChatClient(message, isEmote, isEmote, target);
		}
	}

}
