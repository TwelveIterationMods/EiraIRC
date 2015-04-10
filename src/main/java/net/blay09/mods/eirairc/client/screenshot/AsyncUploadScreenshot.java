package net.blay09.mods.eirairc.client.screenshot;

import net.blay09.mods.eirairc.api.upload.UploadHoster;
import net.blay09.mods.eirairc.api.upload.UploadedFile;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ScreenshotAction;

public class AsyncUploadScreenshot implements Runnable {

	private final UploadHoster hoster;
	private final Screenshot screenshot;
	private final ScreenshotAction followUpAction;
	private boolean complete;
	
	public AsyncUploadScreenshot(UploadHoster hoster, Screenshot screenshot, ScreenshotAction followUpAction) {
		this.hoster = hoster;
		this.screenshot = screenshot;
		this.followUpAction = followUpAction;
		Thread thread = new Thread(this, "ScreenshotUpload");
		thread.start();
	}
	
	@Override
	public void run() {
		UploadedFile uploadedFile = hoster.uploadFile(screenshot.getFile(), ClientGlobalConfig.uploadBufferSize);
		screenshot.setUploadedFile(uploadedFile);
		complete = true;
	}

	public boolean isComplete() {
		return complete;
	}

	public ScreenshotAction getFollowUpAction() {
		return followUpAction;
	}

	public Screenshot getScreenshot() {
		return screenshot;
	}

}
