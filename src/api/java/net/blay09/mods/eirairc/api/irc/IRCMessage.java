package net.blay09.mods.eirairc.api.irc;

public interface IRCMessage {
    String getTagByKey(String key);

    String getPrefix();

    String getCommand();

    String getArg(int i);

    int argLength();
}
