package org.odict.stardict2odict;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ODictXMLConverter {
    private StarDict dict;
    private Map<String, WordPosition> words;
    private Set<String> wordSet;
    private int totalWords;

    private String resolvePOS(String abbrev) {
        return PartOfSpeechResolver.resolve(abbrev);
    }

    ODictXMLConverter(StarDict dict) {
        this.dict = dict;
        this.words = dict.getWords();
        this.wordSet = this.words.keySet();
        this.totalWords = this.words.size();
    }

    private String getExplanation(String word) {
        WordPosition pos = this.words.get(word);
        return pos.getLength() > 0 ? this.dict.getWordExplanation(pos.getStartPos(), pos.getLength()) : null;
    }

    public EntryExample getExample() {
        String firstWord = (String)words.keySet().toArray()[0];
        WordPosition position = words.get(firstWord);
        String explanation = dict.getWordExplanation(position.getStartPos(), position.getLength());
        return new EntryExample(firstWord, explanation);
    }

    private String makeAttribute(String key, String value) {
        return String.format("%s=\"%s\"", key, value);
    }

    private String getMatchGroup(Matcher match, Map<String, String> rules, String key) {
        return match.group(Integer.parseInt(rules.get(key)));
    }

    public String convert(YAMLConfig config) {
        String regex = config.getRegex();
        Map<String, String> rules = config.getMatch();
        String name = config.getName();
        StringBuilder builder = new StringBuilder();

        if (regex == null) {
            System.out.println("Regex is required in the configuration file in order to perform conversion. Did you forget to add a `regex` key?");
            System.exit(1);
        } else if (rules == null) {
            System.out.println("A match key must exist in order to perform conversion. Did you forget to add a `match` key?");
            System.exit(1);
        }

        builder.append("<dictionary ");

        if (name != null) builder.append("name=\"" + name + "\">");
        else builder.append(">");

        int in_count = 0;
        int out_count = 0;

        for (String word : this.wordSet) {
            String exp =  this.getExplanation(word);

            if (exp != null) {
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher match = pattern.matcher(exp);
                String entryContents = "";

                while(match.find()) {
                    ArrayList<String> usageAttrs = new ArrayList<>();

                    if (rules.containsKey(ConfigKeys.KEY_POS))
                        usageAttrs.add(makeAttribute("pos", resolvePOS(
                            this.getMatchGroup(match, rules, ConfigKeys.KEY_POS))
                        ));

                    if (rules.containsKey(ConfigKeys.KEY_ALT))
                        usageAttrs.add(makeAttribute("alt", this.getMatchGroup(match, rules, ConfigKeys.KEY_ALT)));

                    if (rules.containsKey(ConfigKeys.KEY_ROMANIZATION))
                        usageAttrs.add(makeAttribute("romanization", this.getMatchGroup(match, rules, ConfigKeys.KEY_POS)));

                    if (!rules.containsKey(ConfigKeys.KEY_DEFINITION)) {
                        System.out.println("At least a 'definition' matcher key must be specified in order to perform conversion");
                        System.exit(1);
                    }

                    String definition = this.getMatchGroup(match, rules, ConfigKeys.KEY_DEFINITION);

                    entryContents += String.format(String.format("<usage %s>", String.join(" ", usageAttrs)));

                    for (String d : definition.split(config.getDelimiter())) {
                        entryContents += String.format("<definition>%s</definition>", d.trim());
                    }

                    entryContents += "</usage>";
                }

                if (entryContents.length() != 0) {
                    builder.append(String.format("<entry term=\"%s\"><ety>%s</ety></entry>", word, entryContents));
                    in_count++;
                    System.out.print(String.format("\r  ==> Added word %d / %d", in_count, this.totalWords));
                } else {
                    out_count++;
                }
            } else {
                out_count++;
            }
        }

        System.out.print("\n");
        System.out.println(String.format("  ==> Saved %s words", in_count));
        System.out.println(String.format("  ==> Omitted %s words", out_count));

        builder.append("</dictionary>");

        return builder.toString();
    }
}
