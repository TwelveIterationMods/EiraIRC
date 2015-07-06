package net.blay09.mods.eirairc.api.irc;

public interface IRCMessage {
    String getTagByKey(String key);

    String getPrefix();

    String getCommand();

    String arg(int i);

    int argCount();
}
