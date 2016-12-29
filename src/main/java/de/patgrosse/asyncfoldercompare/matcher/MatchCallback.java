package de.patgrosse.asyncfoldercompare.matcher;

public interface MatchCallback<T> {
    void objectsMatched(String matchName, T oldObject, T newObject);
}
