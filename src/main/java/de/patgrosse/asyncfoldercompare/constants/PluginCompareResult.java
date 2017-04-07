package de.patgrosse.asyncfoldercompare.constants;

public enum PluginCompareResult {
    MATCH("Match"),
    DIFFER("Differ"),
    PREFERNEW("Prefer new"),
    PREFEROLD("Prefer old"),
    UNDEFINED("Undefined");

    private String text;

    PluginCompareResult(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
