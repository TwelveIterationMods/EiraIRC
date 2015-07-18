package net.blay09.mods.eirairc.config;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.Map;

public class AuthManager {

    public static class NickServData {
        public final String username;
        public final String password;

        public NickServData(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    private static File mcDataDir;
    private static Map<String, String> serverPasswords = Maps.newHashMap();
    private static Map<String, String> channelPasswords = Maps.newHashMap();
    private static Map<String, NickServData> nickServPasswords = Maps.newHashMap();

    public static String getServerPassword(String identifier) {
        String serverPassword = serverPasswords.get(identifier);
        return serverPassword != null ? serverPassword : "";
    }

    public static NickServData getNickServData(String identifier) {
        return nickServPasswords.get(identifier);
    }

    public static String getChannelPassword(String identifier) {
        String channelPassword = channelPasswords.get(identifier);
        return channelPassword != null ? channelPassword : "";
    }

    public static void putServerPassword(String identifier, String password) {
        if(password == null || password.isEmpty()) {
            serverPasswords.remove(identifier);
        } else {
            serverPasswords.put(identifier, password);
        }
        save();
    }

    public static void putNickServData(String identifier, String username, String password) {
        if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
            nickServPasswords.remove(identifier);
        } else {
            nickServPasswords.put(identifier, new NickServData(username, password));
        }
        save();
    }

    public static void putChannelPassword(String identifier, String password) {
        if(password == null || password.isEmpty()) {
            channelPasswords.remove(identifier);
        } else {
            channelPasswords.put(identifier, password);
        }
        save();
    }

    public static void load(File mcDataDir) {
        AuthManager.mcDataDir = mcDataDir;
        serverPasswords.clear();
        channelPasswords.clear();
        nickServPasswords.clear();
        try {
            File authFile = new File(mcDataDir, "eirairc.auth");
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(new FileReader(authFile), JsonObject.class);
            JsonObject servers = object.getAsJsonObject("servers");
            for(Map.Entry<String, JsonElement> entry : servers.entrySet()) {
                serverPasswords.put(entry.getKey(), entry.getValue().getAsString());
            }
            JsonObject channels = object.getAsJsonObject("channels");
            for(Map.Entry<String, JsonElement> entry : channels.entrySet()) {
                channelPasswords.put(entry.getKey(), entry.getValue().getAsString());
            }
            JsonObject nickserv = object.getAsJsonObject("nickserv");
            for(Map.Entry<String, JsonElement> entry : nickserv.entrySet()) {
                JsonObject obj = entry.getValue().getAsJsonObject();
                nickServPasswords.put(entry.getKey(), new NickServData(obj.get("username").getAsString(), obj.get("password").getAsString()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        JsonObject root = new JsonObject();
        JsonObject servers = new JsonObject();
        for(Map.Entry<String, String> entry : serverPasswords.entrySet()) {
            servers.add(entry.getKey(), new JsonPrimitive(entry.getValue()));
        }
        root.add("servers", servers);
        JsonObject channels = new JsonObject();
        for(Map.Entry<String, String> entry : channelPasswords.entrySet()) {
            channels.add(entry.getKey(), new JsonPrimitive(entry.getValue()));
        }
        root.add("channels", channels);
        JsonObject nickserv = new JsonObject();
        for(Map.Entry<String, NickServData> entry : nickServPasswords.entrySet()) {
            JsonObject obj = new JsonObject();
            obj.add("username", new JsonPrimitive(entry.getValue().username));
            obj.add("password", new JsonPrimitive(entry.getValue().password));
            nickserv.add(entry.getKey(), obj);
        }
        root.add("nickserv", nickserv);
        File authFile = new File(mcDataDir, "eirairc.auth");
        Gson gson = new Gson();
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(authFile));
            writer.setIndent("  ");
            gson.toJson(root, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
