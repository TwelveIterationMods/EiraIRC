// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.upload.UploadHoster;
import net.minecraft.command.ICommandSender;

public class EiraIRCAPI {

	private static InternalMethods internalMethods;

	/**
	 * INTERNAL METHOD. DO NOT CALL.
	 * @param internalMethods implementation of internal API methods
	 */
	public static void internalSetupAPI(InternalMethods internalMethods) {
		if(EiraIRCAPI.internalMethods != null) {
			throw new RuntimeException("EiraIRC API is already initialized");
		}
		EiraIRCAPI.internalMethods = internalMethods;
	}

	/**
	 * Registers the sub-command to EiraIRC's /irc and /sirc commands.
	 * @param command the class implementing the command
	 */
	public static void registerSubCommand(SubCommand command) {
		internalMethods.registerSubCommand(command);
	}

	/**
	 * Registers the upload hoster to EiraIRC's upload manager so that it can be used for screenshot uploading.
	 * @param uploadHoster the class implementing the hoster
	 */
	public static void registerUploadHoster(UploadHoster uploadHoster) {
		internalMethods.registerUploadHoster(uploadHoster);
	}

	/**
	 * @param parentContext the context to search for contextPath in or null
	 * @param contextPath the path to an IRC context (e.g. irc.esper.net/#EiraIRC)
	 * @param expectedType the expected return type or null for any
	 * @return an IRC context or a context of type Error with the error message as it's name
	 */
	public static IRCContext parseContext(IRCContext parentContext, String contextPath, IRCContext.ContextType expectedType) {
		return internalMethods.parseContext(parentContext, contextPath, expectedType);
	}

	/**
	 * @param serverHost an IRC server address (without the port)
	 * @return true if EiraIRC is connected to the given host
	 */
	public static boolean isConnectedTo(String serverHost) {
		return internalMethods.isConnectedTo(serverHost);
	}

	/**
	 * @param user the player to be checked
	 * @return true if the player has EiraIRC installed on the client-side
	 */
	public static boolean hasClientSideInstalled(ICommandSender user) {
		return internalMethods.hasClientSideInstalled(user);
	}

	/**
	 * @param sender the sender of the message - this is ignored on client-side connections (sender is always the player)
	 * @param message the message that should be send to IRC
	 * @param isEmote true if this message should be sent as an ACTION
	 * @param isNotice true if this message should be sent as a NOTICE
	 * @param target the target context of this message (either a channel or a user)
	 */
	public static void relayChat(ICommandSender sender, String message, boolean isEmote, boolean isNotice, IRCContext target) {
		internalMethods.relayChat(sender, message, isEmote, isNotice, target);
	}
}
