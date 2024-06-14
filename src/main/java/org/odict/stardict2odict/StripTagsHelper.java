package org.odict.stardict2odict;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class StripTagsHelper {
    public static String stripTags(String input) {
        input = input.replaceAll("<div>", "\n");
        Document doc = Jsoup.parse(input);
        String cleanedHtml = doc.text();
        return cleanedHtml.trim();
    }
}
