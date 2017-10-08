package com.tehreh1uneh.cloudstorage.server;

import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;

import java.net.Socket;

public class ClientSocketThread extends SocketThread {

    private boolean authorized = false;

    public ClientSocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }


}
