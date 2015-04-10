// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;

public class ScreenshotUploadEvent extends Event {

    public final Screenshot screenshot;

    public ScreenshotUploadEvent(Screenshot screenshot) {
        this.screenshot = screenshot;
    }

}
