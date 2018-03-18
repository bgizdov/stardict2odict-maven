package org.odict.stardict2odict;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YAMLConfig {
    private String regex;
    private String delimiter = ",";
    private String name;
    private Map<String, String> match;

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
}
