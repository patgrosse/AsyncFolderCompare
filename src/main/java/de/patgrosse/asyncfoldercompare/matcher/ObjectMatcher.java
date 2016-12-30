package de.patgrosse.asyncfoldercompare.matcher;

import java.util.Collection;

import de.patgrosse.asyncfoldercompare.constants.MatchStrategy;

public abstract class ObjectMatcher<T> {
    private String name;
    private MatchStrategy strategy;

    public ObjectMatcher(String name, MatchStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
    }

    public String getName() {
        return name;
    }

    public MatchStrategy getStrategy() {
        return strategy;
    }

    public abstract void matchObjects(Collection<T> oldObjects, Collection<T> newObjects, MatchCallback<T> callback);

}
