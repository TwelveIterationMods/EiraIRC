package net.blay09.mods.eirairc.client.screenshot;

import net.blay09.mods.eirairc.api.upload.IUploadHoster;
import net.blay09.mods.eirairc.api.upload.UploadedFile;
import net.blay09.mods.eirairc.config.ScreenshotAction;

public class AsyncUploadScreenshot implements Runnable {

	private final IUploadHoster hoster;
	private final Screenshot screenshot;
	private final ScreenshotAction followUpAction;
	private final Thread thread;
	private boolean complete;
	
	public AsyncUploadScreenshot(IUploadHoster hoster, Screenshot screenshot, ScreenshotAction followUpAction) {
		this.hoster = hoster;
		this.screenshot = screenshot;
		this.followUpAction = followUpAction;
		thread = new Thread(this, "ScreenshotUpload");
		thread.start();
	}
	
	@Override
	public void run() {
		UploadedFile uploadedFile = hoster.uploadFile(screenshot.getFile());
		screenshot.setURL(uploadedFile.url);
		screenshot.setDeleteURL(uploadedFile.deleteURL);
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
