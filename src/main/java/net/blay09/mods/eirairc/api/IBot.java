package net.blay09.mods.eirairc.api;

import net.minecraft.command.ICommandSender;

public interface IBot extends ICommandSender {

	public boolean getBoolean(String key);

	public IIRCConnection getConnection();

	public void resetLog();
	public String getLogContents();

}
