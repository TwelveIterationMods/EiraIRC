package blay09.mods.eirairc.client.upload;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

public class DirectUpload extends UploadHoster {

	public static final String API = "http://www.directupload.net/api/upload.php";
	
	private static final int BUFFER_SIZE = 1024;
	
	@Override
	public String uploadFile(File file) {
		try {
			String boundary = Long.toHexString(System.currentTimeMillis());
			boundary = "---------------------------7d41b838504d8";
			URL apiURL = new URL(API);
			HttpURLConnection con = (HttpURLConnection) apiURL.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Cache-Control", "no-cache");
			
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			out.writeBytes("--" + boundary + "\r\n");
			out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
			out.writeBytes("\r\n");
			
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while((len = fis.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			fis.close();
			
			out.writeBytes("\r\n");
			out.writeBytes("--" + boundary + "--\r\n");
			
			out.flush();
			out.close();
			
			InputStream in = new BufferedInputStream(con.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
				System.out.println(line);
			}
			reader.close();
			in.close();
			con.disconnect();
			return stringBuilder.toString();
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
	
}
