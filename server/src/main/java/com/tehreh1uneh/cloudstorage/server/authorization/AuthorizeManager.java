package com.tehreh1uneh.cloudstorage.server.authorization;

public interface AuthorizeManager {

    void initialize();

    boolean register(String login, String password);
    boolean authorize(String login, String password);

    void dispose();
}
