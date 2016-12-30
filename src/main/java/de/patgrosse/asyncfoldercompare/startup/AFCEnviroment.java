package de.patgrosse.asyncfoldercompare.startup;

import de.patgrosse.asyncfoldercompare.utils.fsthreads.FileActionThreadPoolManager;

public final class AFCEnviroment {

    private AFCEnviroment() {
    }

    public static void initJVM() {
        System.setProperty("jcifs.resolveOrder", "DNS");
        System.setProperty("jcifs.smb.client.dfs.disabled", "true");
        FileActionThreadPoolManager.getInstance().init(true);
    }

}
