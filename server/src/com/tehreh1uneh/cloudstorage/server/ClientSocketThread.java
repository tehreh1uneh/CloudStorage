package com.tehreh1uneh.cloudstorage.server;

import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;

import java.net.Socket;

class ClientSocketThread extends SocketThread {

    private String login;
    private String path;
    private boolean authorized = false;

    ClientSocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }

    String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }

    String getLogin() {
        return login;
    }

    void setLogin(String login) {
        this.login = login;
    }

    boolean isAuthorized() {
        return authorized;
    }

    void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

}
