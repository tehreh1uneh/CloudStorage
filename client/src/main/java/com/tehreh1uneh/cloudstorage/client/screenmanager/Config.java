package com.tehreh1uneh.cloudstorage.client.screenmanager;

public final class Config {

    private Config() {
    }

    static final String DEFAULT_IP = "127.0.0.1";
    static final int DEFAULT_PORT = 8189;

    public static final String MAX_FILE_SIZE_DESCRIPTION = "5 МБ";
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String USER_HOME = System.getProperty("user.home");
    static final String STORAGE_PATH = USER_HOME + FILE_SEPARATOR + "Downloads" + FILE_SEPARATOR + "CloudStorage" + FILE_SEPARATOR;

    static final String AUTH_VIEW_PATH = "/AuthScreen.fxml";
    static final String AUTH_VIEW_TITLE = "Авторизация";
    static final int AUTH_VIEW_WIDTH = 350;
    static final int AUTH_VIEW_HEIGHT = 450;

    static final String MAIN_VIEW_PATH = "/MainScreen.fxml";
    static final String MAIN_VIEW_TITLE = "Cloud Storage";
    static final int MAIN_VIEW_WIDTH = 800;
    static final int MAIN_VIEW_HEIGHT = 600;

    static final String REGISTRATION_VIEW_PATH = "/RegistrationScreen.fxml";
    static final String REGISTRATION_VIEW_TITLE = "Регистрация";

    public static final String ICON_FOLDER_PATH = "/folder_icon.png";
    public static final String FOLDER_DESCRIPTION = "Folder";
    public static final double COLUMN_FOLDER_WIDTH = 30;
    public static final double ICON_FOLDER_RATIO = 0.8;

}
