package org.odict.stardict2odict;

import org.odict.stardict2odict.stardict.StarDict;
import org.odict.stardict2odict.stardict.StarDictParser;

import java.io.IOException;

/**
 * This is not a runnable app, refer package org.yage.dict.star please!
 */
public class Main {
    public static void main(String[] args) throws IOException {
        StarDict dict = new StarDictParser().parse("/Users/tjnickerson/Downloads/stardict-babylon-Babylon_Chinese_S_English-2.4.2.tar.bz2");;
        System.out.println(dict.getWords());
//        WordPosition firstWord = sdp.getWords().get("seslendirmeci");
//        int start = firstWord.getStartPos();
//        int len = firstWord.getLength();
//        String explanation = sdp.getWordExplanation(start, len);
//        System.out.println(explanation);
    }
}
