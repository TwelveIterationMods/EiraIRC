package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCTarget;
import net.blay09.mods.eirairc.irc.IRCUser;

public class IRCResolver {

	public static final short FLAGS_NONE = 0;
	public static final short FLAG_CHANNEL = 2;
	public static final short FLAG_USER = 4;
	public static final short FLAG_ONCHANNEL = 32;
	public static final short FLAG_USERONCHANNEL = 512;

	public static IRCTarget resolveTarget(String path, short flags) {
		String server = null;
		int serverIdx = path.indexOf('/');
		IRCConnection connection = null;
		if(serverIdx != -1) {
			server = path.substring(0, serverIdx);
			path = path.substring(serverIdx + 1);
			connection = EiraIRC.instance.getConnection(server);
			if(connection == null) {
				return IRCTargetError.NotConnected;
			}
		} else {
			IRCConnection foundConnection = null;
			for(IRCConnection con : EiraIRC.instance.getConnections()) {
				if(con.getChannel(path) != null || con.getUser(path) != null) {
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
		if(path.startsWith("#")) {
			if((flags & FLAG_CHANNEL) == 0) {
				return IRCTargetError.InvalidTarget;
			}
			IRCChannel channel = connection.getChannel(path);
			if(channel == null) {
				return IRCTargetError.NotOnChannel;
			}
			return channel;
		} else {
			if((flags & FLAG_USER) == 0) {
				return IRCTargetError.InvalidTarget;
			}
			IRCUser user = connection.getUser(path);
			if(user == null) {
				if((flags & FLAG_USERONCHANNEL) != 0) {
					return IRCTargetError.UserNotFound;
				} else {
					user = connection.getOrCreateUser(path);
				}
			}
			return user;
		}
	}
	
	public static String stripPath(String path) {
		int serverIdx = path.indexOf('/');
		if(serverIdx != -1) {
			return path.substring(serverIdx + 1);
		}
		return path;
	}
	
	public static IRCConnection resolveConnection(String path, short flags) {
		String server = null;
		int serverIdx = path.indexOf('/');
		if(serverIdx != -1) {
			server = path.substring(0, serverIdx);
			path = path.substring(serverIdx + 1);
		} else {
			if(path.startsWith("#")) {
				for(IRCConnection connection : EiraIRC.instance.getConnections()) {
					if(connection.getChannel(path) != null) {
						return connection;
					}
				}
			} else {
				server = path;
			}
		}
		return EiraIRC.instance.getConnection(server);
	}
	
	public static ServerConfig resolveServerConfig(String target, short flags) {
		return null;
	}
	
	public static ChannelConfig resolveChannelConfig(String target, short flags) {
		return null;
	}

	public static boolean hasServerPrefix(String path) {
		return path.indexOf('/') != -1;
	}
	
//	public static Object resolveIRCTarget(String target, boolean allowServers, boolean requireConnected, boolean allowChannels, boolean requireOnChannel, boolean allowUsers, boolean channelUsersOnly) {
//		String server = null;
//		String channel = null;
//		if(target.startsWith("#")) {
//			if(!allowChannels) {
//				return IRCTargetError.InvalidTarget;
//			}
//			channel = target;
//			ChannelConfig foundConfig = null;
//			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
//				if(serverConfig.hasChannelConfig(channel)) {
//					if(foundConfig != null) {
//						return IRCTargetError.SpecifyServer;
//					}
//					foundConfig = serverConfig.getChannelConfig(channel);
//				}
//			}
//			if(foundConfig == null) {
//				if(EiraIRC.instance.getConnectionCount() > 1) {
//					return IRCTargetError.SpecifyServer;
//				} else {
//					foundConfig = ConfigurationHandler.getDefaultServerConfig().getChannelConfig(channel);
//				}
//			}
//			if(requireConnected || requireOnChannel) {
//				ServerConfig serverConfig = foundConfig.getServerConfig();
//				IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
//				if(connection == null) {
//					return IRCTargetError.NotConnected;
//				}
//				if(requireOnChannel) {
//					IRCChannel foundChannel = connection.getChannel(foundConfig.getName());
//					if(foundChannel == null) {
//						return IRCTargetError.NotOnChannel;
//					}
//					return foundChannel;
//				}
//			}
//			return foundConfig;
//		} else {
//			int channelIndex = target.indexOf('/');
//			if(channelIndex != -1 && channelIndex < target.length() - 1) {
//				server = target.substring(0, channelIndex);
//				if(!ConfigurationHandler.hasServerConfig(server)) {
//					return IRCTargetError.ServerNotFound;
//				}
//				ServerConfig serverConfig = ConfigurationHandler.getServerConfig(server);
//				channel = target.substring(channelIndex + 1);
//				if(channel.startsWith("#")) {
//					if(!allowChannels) {
//						return IRCTargetError.InvalidTarget;
//					}
//					if(requireConnected || requireOnChannel) {
//						IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
//						if(connection == null) {
//							return IRCTargetError.NotConnected;
//						}
//						ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
//						if(requireOnChannel) {
//							IRCChannel foundChannel = connection.getChannel(channelConfig.getName());
//							if(foundChannel == null) {
//								return IRCTargetError.NotOnChannel;
//							}
//							return foundChannel;
//						}
//						return channelConfig;
//					}
//				} else {
//					if(!allowUsers) {
//						return IRCTargetError.InvalidTarget;
//					}
//					IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
//					if(connection == null) {
//						return IRCTargetError.NotConnected;
//					}
//					IRCUser user = connection.getUser(channel);
//					if(user == null) {
//						if(channelUsersOnly) {
//							return IRCTargetError.UserNotFound;
//						}
//						return new IRCUser(connection, channel);
//					}
//					return user;
//				}
//			} else {
//				if(target.endsWith("/")) {
//					target = target.substring(0, target.length() - 1);
//				}
//				if(ConfigurationHandler.hasServerConfig(target)) {
//					if(!allowServers) {
//						return IRCTargetError.InvalidTarget;
//					}
//					if(requireConnected) {
//						IRCConnection connection = EiraIRC.instance.getConnection(target);
//						if(connection == null) {
//							return IRCTargetError.NotConnected;
//						}
//						return connection;
//					}
//					return ConfigurationHandler.getServerConfig(target);
//				} else {
//					if(allowUsers) {
//						IRCUser foundUser = null;
//						for(IRCConnection connection : EiraIRC.instance.getConnections()) {
//							IRCUser user = connection.getUser(target);
//							if(user != null) {
//								if(foundUser != null) {
//									return IRCTargetError.SpecifyServer;
//								}
//								foundUser = user;
//							}
//						}
//						if(foundUser == null) {
//							if(channelUsersOnly) {
//								return IRCTargetError.UserNotFound;
//							} else {
//								if(EiraIRC.instance.getConnectionCount() > 1) {
//									return IRCTargetError.SpecifyServer;
//								} else {
//									return new IRCUser(EiraIRC.instance.getDefaultConnection(), target);
//								}
//							}
//						}
//						return foundUser;
//					}
//					if(allowServers && !allowUsers) {
//						return IRCTargetError.ServerNotFound;
//					} else if(allowUsers && !allowServers) {
//						return IRCTargetError.UserNotFound;
//					}
//				}
//			}
//		}
//		return IRCTargetError.TargetNotFound;
//	}
	
}
