// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.config;

import java.util.ArrayList;
import java.util.List;

public class GlobalConfig {

	public static final String MC_CMESSAGE_FORMAT_NORMAL = "[{CHANNEL}] <{NICK}> {MESSAGE}";
	public static final String MC_CMESSAGE_FORMAT_LIGHT = "[#] <{NICK}> {MESSAGE}";
	public static final String MC_CMESSAGE_FORMAT_SLIGHT = "[{NICK}] {MESSAGE}";
	public static final String MC_CMESSAGE_FORMAT_DETAIL = "[{SERVER}:{CHANNEL}] <{NICK}> {MESSAGE}";
	public static final String MC_CEMOTE_FORMAT_NORMAL = "[{CHANNEL}] * {NICK} {MESSAGE}";
	public static final String MC_PMESSAGE_FORMAT_NORMAL = "[Private] <{NICK}> {MESSAGE}";
	public static final String MC_PMESSAGE_FORMAT_LIGHT = "[P] <{NICK}> {MESSAGE}";
	public static final String MC_PMESSAGE_FORMAT_SLIGHT = "[[{NICK}]] {MESSAGE}";
	public static final String MC_PMESSAGE_FORMAT_DETAIL = "[{SERVER}] <{NICK}> {MESSAGE}";
	public static final String MC_PEMOTE_FORMAT_NORMAL = "* {NICK} {MESSAGE}";
	public static final String IRC_CMESSAGE_FORMAT_NORMAL = "<{NICK}> {MESSAGE}";
	public static final String IRC_CMESSAGE_FORMAT_DETAIL = "[{SERVER}] <{NICK}> {MESSAGE}";
	public static final String IRC_CEMOTE_FORMAT_NORMAL = "{NICK} {MESSAGE}";
	public static final String IRC_PMESSAGE_FORMAT_NORMAL = "<{NICK}> {MESSAGE}";
	public static final String IRC_PMESSAGE_FORMAT_DETAIL = "[{SERVER}] <{NICK}> {MESSAGE}";
	public static final String IRC_PEMOTE_FORMAT_NORMAL = "{NICK} {MESSAGE}";
	
	public static String nick = ConfigurationHandler.DEFAULT_NICK;
	public static final List<String> colorBlackList = new ArrayList<String>();
	public static String opColor = "red";
	public static String ircColor = "darkgray";
	public static boolean enableNameColors = true;
	public static boolean enableAliases = true;
	public static boolean showDeathMessages = true;
	public static boolean showMinecraftJoinLeave = true;
	public static boolean showIRCJoinLeave = true;
	public static boolean allowPrivateMessages = true;
	public static boolean persistentConnection = true;
	public static boolean showNickChanges = true;
	public static boolean enableLinkFilter = true;
	public static String mcChannelMsgFormat = MC_CMESSAGE_FORMAT_NORMAL;
	public static String mcPrivateMsgFormat = MC_PMESSAGE_FORMAT_NORMAL;
	public static String mcChannelEmtFormat = MC_CEMOTE_FORMAT_NORMAL;
	public static String mcPrivateEmtFormat = MC_PEMOTE_FORMAT_NORMAL;
	public static String ircChannelMsgFormat = IRC_CMESSAGE_FORMAT_NORMAL;
	public static String ircPrivateMsgFormat = IRC_PMESSAGE_FORMAT_NORMAL;
	public static String ircChannelEmtFormat = IRC_CEMOTE_FORMAT_NORMAL;
	public static String ircPrivateEmtFormat = IRC_PEMOTE_FORMAT_NORMAL;
	public static boolean interOp = false;
	
}
