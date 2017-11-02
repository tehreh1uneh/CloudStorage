package com.tehreh1uneh.cloudstorage.server;

final class Config {

    static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String SYSTEM_ROOT = System.getenv("SystemDrive");
    static final String STORAGE_PATH = SYSTEM_ROOT + FILE_SEPARATOR + "CloudStorage_files" + FILE_SEPARATOR;

    private Config() {
    }
}
