// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonElement;
import net.blay09.mods.eirairc.api.upload.IUploadHoster;
import net.blay09.mods.eirairc.api.upload.UploadedFile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import org.jetbrains.annotations.Nullable;

public class ImgurHoster implements IUploadHoster {

	private static final String API = "https://api.imgur.com/3/image.json";
	private static final String IMAGE_BASE_URL = "https://imgur.com/";
	private static final String API_CLIENT_ID = "d47c0303c944643";
	
	@Override
	public UploadedFile uploadFile(File file) {
		if(!file.exists()) {
			return null;
		}
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(API).openConnection();
			con.setDoOutput(true);
			con.setRequestProperty("Authorization", "Client-ID " + API_CLIENT_ID);
			
			OutputStream out = con.getOutputStream();
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[ClientGlobalConfig.uploadBufferSize];
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

	@Override
	public boolean isCustomizable() {
		return false;
	}

	@Nullable
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
			System.out.println("Uploaded image to imgur at " + result + " (delete hash: " + deleteHash + ")");
			return result;
		}
		return null;
	}
	
	private void handleError(InputStream in) {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(in);
		while(scanner.hasNext()) {
			sb.append(scanner.next());
		}
		scanner.close();
		System.out.println("Failed to upload to imgur: " + sb.toString());
	}

	@Override
	public String getName() {
		return "imgur";
	}
	
}
