package blay09.mods.eirairc.client.upload;

import java.io.File;

public abstract class UploadHoster {

	public static final String[] availableHosters = new String[] {
		"DirectUpload"
	};
	
	
	public abstract String uploadFile(File file);
	public abstract boolean isCustomizable();
	
	private static String cachedHosterName;
	private static UploadHoster cachedHoster;
	public static UploadHoster getUploadHoster(String name) {
		if(cachedHoster != null && cachedHosterName.equals(name)) {
			return cachedHoster;
		}
		try {
			Class<?> clazz = Class.forName("blay09.mods.eirairc.client.upload." + name);
			if(UploadHoster.class.isAssignableFrom(clazz)) {
				cachedHoster = (UploadHoster) clazz.newInstance();
				cachedHosterName = name;
				return cachedHoster;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
