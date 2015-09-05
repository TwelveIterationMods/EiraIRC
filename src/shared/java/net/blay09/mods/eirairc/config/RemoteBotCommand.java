package net.blay09.mods.eirairc.config;

import com.google.gson.JsonObject;

public class RemoteBotCommand {

    public final String command;
    public final boolean requireOp;

    public RemoteBotCommand(String command, boolean requireOp) {
        this.command = command;
        this.requireOp = requireOp;
    }

    public static RemoteBotCommand loadFromJson(JsonObject obj) {
        return new RemoteBotCommand(obj.get("name").getAsString(), !obj.has("requireAuth") || obj.get("requireAuth").getAsBoolean());
    }

}
