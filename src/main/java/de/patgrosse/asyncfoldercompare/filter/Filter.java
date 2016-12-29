package de.patgrosse.asyncfoldercompare.filter;

public abstract class Filter<T> {

    public abstract boolean isObjectFiltered(T object);

}
