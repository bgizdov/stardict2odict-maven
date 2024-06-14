package org.odict.stardict2odict;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
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

    public List<EntryExample> getExample() {
        ArrayList<EntryExample> examples = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            String word = (String)words.keySet().toArray()[i];
            WordPosition position = words.get(word);
            String explanation = dict.getWordExplanation(position.getStartPos(), position.getLength());
            examples.add(new EntryExample(word, explanation));
        }

        return examples;
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

            List<String> ommitedWords = new ArrayList<>();
        for (String word : this.wordSet) {
            System.out.println("Processing word: " + word);
            String exp =  this.getExplanation(word);
            System.out.println("Explanation: " + exp);

            if (exp != null) {
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher match = pattern.matcher(exp);
                StringBuilder entryContentsBuilder = new StringBuilder();

                if (config.isStripTags()) {
                    String definition = StripTagsHelper.stripTags(exp);
                System.out.println("Definition: " + definition);
                    addDefinition(config, entryContentsBuilder, definition, new ArrayList<>());
                } else {
                    extractWithRegex(config, match, rules, entryContentsBuilder);
                }

                String entryContents = entryContentsBuilder.toString();

                if (entryContents.length() != 0) {
                    builder.append(String.format("<entry term=\"%s\"><ety>%s</ety></entry>", word, entryContents));
                    in_count++;
                    System.out.print(String.format("\r  ==> Added word %d / %d", in_count, this.totalWords));
                } else {
                    out_count++;
                    ommitedWords.add(word);
                }
            } else {
                out_count++;
                ommitedWords.add(word);
            }
        }

        System.out.print("\n");
        System.out.println(String.format("  ==> Saved %s words", in_count));
        System.out.println(String.format("  ==> Omitted %s words", out_count));
        System.out.println("  ==> Omitted words: " + String.join(", ", ommitedWords));

        builder.append("</dictionary>");

        return builder.toString();
    }

    private void extractWithRegex(YAMLConfig config, Matcher match, Map<String, String> rules, StringBuilder entryContentsBuilder) {
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

            addDefinition(config, entryContentsBuilder, definition, usageAttrs);
        }
    }

    private static void addDefinition(YAMLConfig config, StringBuilder entryContentsBuilder, String definition, ArrayList<String> usageAttrs) {
        if (definition.trim().length() > 0) {
            String attrs = String.join(" ", usageAttrs).trim();

            if (attrs.length() > 0)
                entryContentsBuilder.append(String.format("<usage %s>", attrs));
            else entryContentsBuilder.append("<usage>");

            for (String d : definition.split(config.getDelimiter())) {
                String trimmed = StringEscapeUtils.escapeHtml4(d.trim());
                if (trimmed.length() > 0)
                    entryContentsBuilder.append(String.format("<definition>%s</definition>", trimmed));
            }

            entryContentsBuilder.append("</usage>");
        }
    }
}
