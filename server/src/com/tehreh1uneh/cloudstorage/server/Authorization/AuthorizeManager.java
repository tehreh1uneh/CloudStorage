package com.tehreh1uneh.cloudstorage.server.Authorization;

public interface AuthorizeManager {

    void initialize();

    boolean authorize(String login, String password);

    void dispose();
}
