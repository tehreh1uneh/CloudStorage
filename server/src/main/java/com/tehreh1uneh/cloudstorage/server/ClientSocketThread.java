package com.tehreh1uneh.cloudstorage.server;

import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;

import java.net.Socket;
import java.nio.file.Path;

class ClientSocketThread extends SocketThread {

    private String login;
    private Path path;
    private Path currentPath;
    private boolean authorized = false;

    ClientSocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }

    Path getCurrentPath() {
        if (currentPath == null) currentPath = path;
        return currentPath;
    }

    void setCurrentPath(Path currentPath) {
        if (!currentPath.startsWith(path)) {
            this.currentPath = path;
        } else {
            this.currentPath = currentPath;
        }
    }

    boolean currentIsRoot() {
        return path.equals(currentPath);
    }

    Path getPath() {
        return path;
    }

    void setPath(Path path) {
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
