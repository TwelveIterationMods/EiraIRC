package net.blay09.mods.eirairc.config.property;

import java.util.ArrayList;

public class StringList extends ArrayList<String> {

    public StringList(String... values) {
        for(String value : values) {
            add(value);
        }
    }

    public boolean stringContains(String s, boolean allowWildcard) {
        for(String entry : this) {
            if (allowWildcard && entry.equals("*")) {
                return true;
            }
            if (s.contains(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsString(String s, boolean allowWildcard) {
        for(String entry : this) {
            if (allowWildcard && entry.equals("*")) {
                return true;
            }
            if (entry.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public String[] getAsArray() {
        return toArray(new String[size()]);
    }

}
