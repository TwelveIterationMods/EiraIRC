// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.blay09.mods.eirairc.api.upload.UploadedFile;

public class ScreenshotUploadEvent extends Event {

    public final UploadedFile uploadedFile;

    public ScreenshotUploadEvent(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

}
