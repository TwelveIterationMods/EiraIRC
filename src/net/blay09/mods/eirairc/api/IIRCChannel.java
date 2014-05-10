// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api;

import java.util.Collection;

public interface IIRCChannel extends IIRCContext {

	public String getTopic();
	public Collection<IIRCUser> getUserList();

}
