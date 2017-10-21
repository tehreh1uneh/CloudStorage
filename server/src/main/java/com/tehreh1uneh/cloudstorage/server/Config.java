package com.tehreh1uneh.cloudstorage.server;

class Config {
    static final String FILE_SEPARATOR = System.getProperty("file.separator");
    static final String USER_HOME = System.getProperty("user.home");
    static final String STORAGE_PATH = USER_HOME + FILE_SEPARATOR + "CloudStorage_files" + FILE_SEPARATOR;
}
