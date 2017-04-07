package de.patgrosse.asyncfoldercompare.utils.fsthreads;

import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyCallbackFactory;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.FinishableCallback;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class FileActionThreadPoolManager {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FileActionThreadPoolManager.class);
    private static FileActionThreadPoolManager instance;

    private boolean initialized;
    private boolean queueMode;
    private CopyMultiFileMultiTaskThread queueThread;
    private Set<AFCThread> parallelPool;

    private FileActionThreadPoolManager() {
        parallelPool = Collections.synchronizedSet(new HashSet<AFCThread>());
    }

    public static FileActionThreadPoolManager getInstance() {
        if (instance == null) {
            instance = new FileActionThreadPoolManager();
        }
        return instance;
    }

    public void init(boolean queueMode) {
        if (initialized) {
            throw new IllegalStateException();
        }
        initialized = true;
        this.queueMode = queueMode;
    }

    public void startCopy(QueuedCopyTask task, CopyCallbackFactory callbackFactory) {
        if (!initialized) {
            throw new IllegalStateException();
        }
        if (queueMode) {
            boolean threadNotYetStarted = (queueThread == null);
            if (threadNotYetStarted) {
                queueThread = new CopyMultiFileMultiTaskThread(callbackFactory.getMultiTaskCallback(task));
            }
            queueThread.queue(task);
            if (threadNotYetStarted) {
                startThread(queueThread);
            }
        } else {
            CopyMultiFileSingleTaskThread thread = new CopyMultiFileSingleTaskThread(task,
                    callbackFactory.getSingleTaskCallback(task));
            startThread(thread);
        }
    }

    public boolean actionsRunning() {
        if (queueMode) {
            return queueThread != null;
        } else {
            return !parallelPool.isEmpty();
        }
    }

    private void startThread(AFCThread thread) {
        if (thread == null) {
            throw new IllegalArgumentException();
        }
        thread.setFinishedCallback(new AFCThreadPoolDeletionCallback(thread));
        thread.start();
        parallelPool.add(thread);
        if (LOG.isInfoEnabled()) {
            LOG.info(thread.getName() + " was started");
        }
    }

    private class AFCThreadPoolDeletionCallback implements FinishableCallback {
        private AFCThread thread;

        public AFCThreadPoolDeletionCallback(AFCThread thread) {
            this.thread = thread;
        }

        @Override
        public void finished(boolean crashed, Exception e) {
            if (queueMode) {
                queueThread = null;
            } else {
                parallelPool.remove(thread);
            }
            if (crashed) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Thread " + thread.getName() + " crashed", e);
                }
            } else {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Thread " + thread.getName() + " finished");
                }
            }
        }
    }

}
