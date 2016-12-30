package de.patgrosse.asyncfoldercompare.utils.fsthreads;

import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.FinishableCallback;

public abstract class AFCThread extends Thread {
    private boolean cancelRequested;
    private FinishableCallback callback;

    public void requestCancel() {
        cancelRequested = true;
    }

    public boolean isCancelRequested() {
        return cancelRequested;
    }

    public void setFinishedCallback(FinishableCallback callback) {
        this.callback = callback;
    }

    @Override
    public final void run() {
        perform();
        if (callback != null) {
            callback.finished(false, null);
        }
    }

    public abstract void perform();
}
