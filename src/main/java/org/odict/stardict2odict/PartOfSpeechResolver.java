package org.odict.stardict2odict;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class PartOfSpeechResolver {
    static private Map<String, String> res;

    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("n", "noun");
        tmp.put("adj", "adjective");
        tmp.put("adv", "adverb");
        tmp.put("v", "verb");
        tmp.put("prep", "preposition");
        tmp.put("pron", "pronoun");
        tmp.put("conj", "conjunction");
        tmp.put("interj", "interjection");
        res = Collections.unmodifiableMap(tmp);
    }

    static String resolve(String partOfSpeech) {
        if (res.containsKey(partOfSpeech))
            return res.get(partOfSpeech);
        return partOfSpeech;
    }
}
