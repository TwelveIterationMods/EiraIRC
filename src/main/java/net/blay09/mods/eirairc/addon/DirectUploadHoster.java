// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.addon;

import net.blay09.mods.eirairc.api.upload.IUploadHoster;
import net.blay09.mods.eirairc.api.upload.UploadedFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DirectUploadHoster implements IUploadHoster {

	public static final String API = "http://www.directupload.net/api/upload.php";
	public static final String BOUNDARY = "---------------------------7d41b838504d8";
	
	@Override
	public UploadedFile uploadFile(File file, int uploadBufferSize) {
		try {
			URL apiURL = new URL(API);
			HttpURLConnection con = (HttpURLConnection) apiURL.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Cache-Control", "no-cache");
			
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			out.writeBytes("--" + BOUNDARY + "\r\n");
			out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
			out.writeBytes("\r\n");
			
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[uploadBufferSize];
			int len;
			while((len = fis.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			fis.close();
			
			out.writeBytes("\r\n");
			out.writeBytes("--" + BOUNDARY + "--\r\n");
			
			out.flush();
			out.close();
			
			InputStream in = new BufferedInputStream(con.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			reader.close();
			in.close();
			con.disconnect();
			return new UploadedFile(stringBuilder.toString(), null, null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isCustomizable() {
		return false;
	}

	@Override
	public String getName() {
		return "DirectUpload";
	}
	
}
