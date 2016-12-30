package de.patgrosse.asyncfoldercompare.utils.fsthreads;

import org.apache.commons.vfs2.FileObject;

import de.patgrosse.asyncfoldercompare.constants.CopyAction;

public class QueuedCopyTask {
    private FileObject source, destination;
    private CopyAction action;

    public QueuedCopyTask(FileObject source, FileObject destination, CopyAction action) {
        this.source = source;
        this.destination = destination;
        this.action = action;
    }

    public FileObject getSource() {
        return source;
    }

    public FileObject getDestination() {
        return destination;
    }

    public CopyAction getAction() {
        return action;
    }
}
