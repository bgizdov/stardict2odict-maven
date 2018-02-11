package org.odict.stardict2odict;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ODictXMLConverter {
    private String regex = "<font color=\"blue\">([a-z]+?)\\.</font>\\s*\\(<I>\\w+?</I>=(.*?)<I>,\\s*Pinyin</I>=(.*?)\\)\\s*(.*?)$";
    private String resolvePOS(String abbrev) {
        return abbrev;
    }

    public String convert(StarDict dict) {
        String xml = "<dictionary>";
        Map<String, WordPosition> allWords = dict.getWords();
        int in_count = 0;
        int out_count = 0;
        int total = allWords.size();

        for (String word : allWords.keySet()) {
            WordPosition position = allWords.get(word);
            if (position.getLength() > 0) {
                String explanation = dict.getWordExplanation(position.getStartPos(), position.getLength());
                Pattern pattern = Pattern.compile(this.regex, Pattern.CASE_INSENSITIVE);
                Matcher match = pattern.matcher(explanation);

                xml += String.format("<entry term=\"%s\"><ety>", word);

                while(match.find()) {
                    String pos = resolvePOS(match.group(1));
                    String alternative = match.group(2);
                    String romanization = match.group(3);
                    String definition = match.group(4);

                    xml += String.format("<usage pos=\"%s\">", pos);

                    for (String d : definition.split(",")) {
                        xml += String.format("<definition>%s</definition>", d.trim());
                    }

                    xml += "</usage>";
                }

                xml += "</ety></entry>";

                in_count++;

                System.out.print(String.format("\rAdded word %d / %d", in_count, total));
            } else {
                out_count++;
            }
        }

        System.out.println(in_count);
        System.out.println(out_count);

        xml += "</dictionary>";

        return xml;
    }
}
