// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;

public class IgnoreList {

    private static final Logger logger = LogManager.getLogger();
    private static final Multimap<String, String> ignoreList = ArrayListMultimap.create();
    private static File configDir;

    public static void load(File baseConfigDir) {
        ignoreList.clear();
        IgnoreList.configDir = baseConfigDir;
        Gson gson = new Gson();
        try {
            Reader reader = new FileReader(new File(baseConfigDir, "ignore_list.json"));
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(true);
            JsonObject servers = gson.fromJson(jsonReader, JsonArray.class);
            for(Map.Entry<String, JsonElement> entry : servers.entrySet()) {
                JsonArray users = entry.getValue().getAsJsonArray();
                for(int i = 0; i < users.size(); i++) {
                    ignoreList.put(entry.getKey(), users.get(i).getAsString());
                }
            }
            reader.close();
        } catch (JsonSyntaxException e) {
            logger.error("Syntax error in ignore_list.json: ", e);
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        Gson gson = new Gson();
        try {
            JsonObject servers = new JsonObject();
            for(String server : ignoreList.keySet()) {
                JsonArray users = new JsonArray();
                for(String user : ignoreList.get(server)) {
                    users.add(new JsonPrimitive(user));
                }
                servers.add(server, users);
            }
            JsonWriter writer = new JsonWriter(new FileWriter(new File(configDir, "ignore_list.json")));
            writer.setIndent("  ");
            gson.toJson(servers, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToIgnoreList(IRCUser user) {
        if(!isIgnored(user)) {
            ignoreList.put(user.getConnection().getIdentifier(), getHostMask(user));
            save();
        }
    }

    public static void removeFromIgnoreList(IRCUser user) {
        ignoreList.remove(user.getConnection().getIdentifier(), getHostMask(user));
        save();
    }

    public static boolean isIgnored(IRCUser user) {
        return ignoreList.containsEntry(user.getConnection().getIdentifier(), getHostMask(user));
    }

    private static String getHostMask(IRCUser user) {
        return "*!*@" + user.getHostname();
    }
}
