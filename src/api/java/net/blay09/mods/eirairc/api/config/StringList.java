package net.blay09.mods.eirairc.api.config;

import java.util.ArrayList;

/**
 * Utility class to keep track of the StringList property (String[] in Forge Configuration)
 */
public class StringList extends ArrayList<String> {

    /**
     * Creates a new StringList with the given values inside.
     * @param values the values to fill the StringList with
     */
    public StringList(String... values) {
        for(String value : values) {
            add(value);
        }
    }

    /**
     * Returns true if the given string contains one of the entries in this StringList.
     * The wildcard '*' isn't a real wildcard, it only works as an 'all' by itself.
     * @param s the string to search in
     * @param allowWildcard true if wildcards ('*') are accepted
     * @return true if the given string contains one of the entries in this StringList
     */
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

    /**
     * Returns true if this StringList contains the given string.
     * The wildcard '*' isn't a real wildcard, it only works as an 'all' by itself.
     * @param s the string to search for
     * @param allowWildcard true if wildcards ('*') are accepted
     * @return true if this StringList contains the given string.
     */
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

    /**
     * @return this StringList as a String array.
     */
    public String[] getAsArray() {
        return toArray(new String[size()]);
    }

}
