package de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks;

public interface FinishableCallback {
    void finished(boolean crashed, Exception e);
}