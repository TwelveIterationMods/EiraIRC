package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.upload.UploadHoster;
import net.minecraft.command.ICommandSender;

/**
 * Created by Blay09 on 23.02.2015.
 */
public interface InternalMethods {
	public boolean isConnectedTo(String serverHost);
	public IRCContext parseContext(IRCContext parentContext, String contextPath, IRCContext.ContextType expectedType);
	public void registerSubCommand(SubCommand command);
	public void registerUploadHoster(UploadHoster uploadHoster);
	public boolean hasClientSideInstalled(ICommandSender user);
}
