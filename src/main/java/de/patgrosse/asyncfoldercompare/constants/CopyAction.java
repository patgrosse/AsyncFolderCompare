package de.patgrosse.asyncfoldercompare.constants;

public enum CopyAction {
    OLDTONEW("Old to New"),
    NEWTOOLD("New to Old"),
    OLDTOTARGET("Old to Target"),
    NEWTOTARGET("New to Target");

    private String text;

    CopyAction(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}