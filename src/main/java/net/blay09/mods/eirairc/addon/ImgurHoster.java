// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.addon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blay09.mods.eirairc.api.upload.UploadHoster;
import net.blay09.mods.eirairc.api.upload.UploadedFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ImgurHoster implements UploadHoster {

	private static final Logger logger = LogManager.getLogger();

	private static final String API = "https://api.imgur.com/3/image.json";
	private static final String IMAGE_BASE_URL = "https://imgur.com/";
	private static final String API_CLIENT_ID = "d47c0303c944643";
	
	@Override
	public UploadedFile uploadFile(File file, int uploadBufferSize) {
		if(!file.exists()) {
			return null;
		}
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(API).openConnection();
			con.setDoOutput(true);
			con.setRequestProperty("Authorization", "Client-ID " + API_CLIENT_ID);
			
			OutputStream out = con.getOutputStream();
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[uploadBufferSize];
			int len;
			while((len = fis.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			fis.close();
			out.flush();
			out.close();
			
			String result = null;
			if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				result = handleResponse(con.getInputStream());
			} else {
				handleError(con.getErrorStream());
			}
			con.disconnect();
			if(result != null) {
				return new UploadedFile(result, result + ".png", null);
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String handleResponse(InputStream in) {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(in);
		while(scanner.hasNext()) {
			sb.append(scanner.next());
		}
		scanner.close();
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(sb.toString());
		if(element.isJsonObject()) {
			JsonObject root = element.getAsJsonObject();
			JsonObject data = root.getAsJsonObject("data");
			String imageId = data.get("id").getAsString();
			String deleteHash = data.get("deletehash").getAsString();
			String result = IMAGE_BASE_URL + imageId;
			logger.info("Uploaded image to imgur at {} (delete hash: {})", result, deleteHash);
			return result;
		}
		logger.error("Upload failed due to invalid response: {}", sb.toString());
		return null;
	}
	
	private void handleError(InputStream in) {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(in);
		while(scanner.hasNext()) {
			sb.append(scanner.next());
		}
		scanner.close();
		logger.error("Failed to upload to imgur: {}", sb.toString());
	}

	@Override
	public String getName() {
		return "imgur";
	}
	
}
