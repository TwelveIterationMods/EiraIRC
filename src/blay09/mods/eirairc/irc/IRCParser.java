package blay09.mods.eirairc.irc;

import java.util.ArrayList;
import java.util.List;

public class IRCParser {

	private final List<String> args = new ArrayList<String>();
	private String data;
	private int idx;
	
	public IRCMessage parse(String data) {
		this.data = data;
		String prefix = null;
		String cmd = null;
		if(data.startsWith(":")) {
			skip(1);
			prefix = next();
		}
		cmd = next();
		
		String arg = null;
		while((arg = next()) != null) {
			args.add(arg);
		}
		IRCMessage msg = new IRCMessage(prefix, cmd, args.toArray(new String[args.size()]));
		reset();
		return msg;
	}
	
	public void reset() {
		data = null;
		idx = 0;
		args.clear();
	}
	
	public void skip(int x) {
		idx += x;
	}
	
	public String next() {
		if(idx >= data.length() - 1) {
			return null;
		}
		int nextIdx = data.indexOf(' ', idx);
		if(nextIdx == -1) {
			nextIdx = data.length();
		}
		if(data.charAt(idx) == ':') {
			idx++;
			nextIdx = data.length();
		}
		String s = data.substring(idx, nextIdx);
		idx = nextIdx + 1;
		return s;
	}
}
