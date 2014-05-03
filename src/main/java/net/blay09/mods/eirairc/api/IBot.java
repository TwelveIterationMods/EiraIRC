package net.blay09.mods.eirairc.api;

import net.minecraft.command.ICommandSender;

public interface IBot extends ICommandSender {

	public boolean getBoolean(String key, boolean defaultVal);

	public IIRCConnection getConnection();

	public void resetLog();
	public String getLogContents();
	public boolean processCommand(IIRCChannel channel, IIRCUser sender, String message);

}
