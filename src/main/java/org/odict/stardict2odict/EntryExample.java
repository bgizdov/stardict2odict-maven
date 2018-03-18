package org.odict.stardict2odict;

public class EntryExample {
    private String word;
    private String explanation;

    EntryExample(String word, String explanation) {
        this.word = word;
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getWord() {
        return word;
    }
}
