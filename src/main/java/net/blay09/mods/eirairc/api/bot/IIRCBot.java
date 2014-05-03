package net.blay09.mods.eirairc.api.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.minecraft.command.ICommandSender;

public interface IIRCBot extends ICommandSender {

	public IIRCConnection getConnection();
	public void resetLog();
	public String getLogContents();
	public boolean processCommand(IIRCChannel channel, IIRCUser sender, String message);
	public IBotProfile getMainProfile();
	public IBotProfile getProfile(IIRCContext channel);
	public boolean getBoolean(IIRCContext context,String string, boolean defaultVal);
	public boolean isMuted(IIRCContext context);
	public boolean isReadOnly(IIRCContext context);
	public boolean isServerSide();
	
}
