package com.tehreh1uneh.cloudstorage.server;

import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;

import java.net.Socket;

class ClientSocketThread extends SocketThread {

    private boolean authorized = false;

    ClientSocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }

    boolean isAuthorized() {
        return authorized;
    }

    void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

}
