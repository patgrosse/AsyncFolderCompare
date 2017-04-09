package de.patgrosse.asyncfoldercompare.plugins.entities;

import java.util.function.Function;

public class CompareCheck {
    private String keyName;
    private String displayName;
    private Function<String, String> formatOutput;

    public CompareCheck(String keyName, String displayName, Function<String, String> formatOutput) {
        this.keyName = keyName;
        this.displayName = displayName;
        this.formatOutput = formatOutput;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Function<String, String> getFormatOutput() {
        return formatOutput;
    }

    public String doFormatOutput(String input) {
        return formatOutput.apply(input);
    }

    public void setFormatOutput(Function<String, String> formatOutput) {
        this.formatOutput = formatOutput;
    }
}
