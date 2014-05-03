package net.blay09.mods.eirairc.api;

import java.util.Collection;

public interface IIRCChannel extends IIRCContext {

	public String getTopic();
	public Collection<IIRCUser> getUserList();

}
