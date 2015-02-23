package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.upload.UploadHoster;

/**
 * Created by Blay09 on 23.02.2015.
 */
public class EiraIRCAPI {

	private static InternalMethods internalMethods;

	/**
	 * INTERNAL METHOD. DO NOT CALL.
	 * @param internalMethods
	 */
	public static void setupAPI(InternalMethods internalMethods) {
		EiraIRCAPI.internalMethods = internalMethods;
	}

	public static void registerSubCommand(SubCommand command) {
		internalMethods.registerSubCommand(command);
	}

	public static void registerUploadHoster(UploadHoster uploadHoster) {
		internalMethods.registerUploadHoster(uploadHoster);
	}

	public static IRCContext parseContext(String contextPath) {
		return internalMethods.parseContext(contextPath);
	}

	public static boolean isConnectedTo(String serverHost) {
		return internalMethods.isConnectedTo(serverHost);
	}
}
