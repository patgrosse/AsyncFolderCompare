package de.patgrosse.asyncfoldercompare.constants;

public enum CompleteObjectCompareResult {
    MATCH("Match"),
    DIFFER("Differ"),
    NEW("New"),
    DELETED("Deleted"),
    PREFERNEW("Prefer new"),
    PREFEROLD("Prefer old"),
    UNDEFINED("Undefined");

    private String text;

    CompleteObjectCompareResult(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
