package org.odict.stardict2odict;

import java.util.Map;

public class YAMLConfig {
    private String regex;
    private String delimiter = ",";
    private String name;
    private Map<String, String> match;

    private boolean stripTags = false;

    public String getRegex() {
        return regex;
    }

    public Map<String, String> getMatch() {
        return match;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getName() {
        return name;
    }

    public boolean isStripTags() {
        return stripTags;
    }

    public void setStripTags(boolean stripTags) {
        this.stripTags = stripTags;
    }
}
