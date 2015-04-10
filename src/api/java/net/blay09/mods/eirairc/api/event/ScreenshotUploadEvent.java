// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.upload.UploadedFile;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ScreenshotUploadEvent extends Event {

    public final UploadedFile uploadedFile;

    public ScreenshotUploadEvent(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

}
