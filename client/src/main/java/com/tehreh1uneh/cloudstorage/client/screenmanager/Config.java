package com.tehreh1uneh.cloudstorage.client.screenmanager;

public class Config {
    static final String DEFAULT_IP = "127.0.0.1";
    static final int DEFAULT_PORT = 8189;

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String USER_HOME = System.getProperty("user.home");
    static final String STORAGE_PATH = USER_HOME + FILE_SEPARATOR + "Downloads" + FILE_SEPARATOR + "CloudStorage" + FILE_SEPARATOR;
}
