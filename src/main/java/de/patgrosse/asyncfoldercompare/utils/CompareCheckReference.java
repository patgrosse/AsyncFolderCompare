package de.patgrosse.asyncfoldercompare.utils;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CompareCheckReference {
    @Expose
    private String pluginRef;
    @Expose
    private String checkRef;

    public CompareCheckReference(String pluginRef, String checkRef) {
        if (pluginRef == null || checkRef == null) {
            throw new IllegalArgumentException();
        }
        this.pluginRef = pluginRef;
        this.checkRef = checkRef;
    }

    public String getPluginRef() {
        return pluginRef;
    }

    public void setPluginRef(String pluginRef) {
        if (pluginRef == null) {
            throw new IllegalArgumentException();
        }
        this.pluginRef = pluginRef;
    }

    public String getCheckRef() {
        return checkRef;
    }

    public void setCheckRef(String checkRef) {
        if (checkRef == null) {
            throw new IllegalArgumentException();
        }
        this.checkRef = checkRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompareCheckReference that = (CompareCheckReference) o;

        return pluginRef.equals(that.pluginRef) && checkRef.equals(that.checkRef);
    }

    @Override
    public int hashCode() {
        int result = pluginRef.hashCode();
        result = 31 * result + checkRef.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
