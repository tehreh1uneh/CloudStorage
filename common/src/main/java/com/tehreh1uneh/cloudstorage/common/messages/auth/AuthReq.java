package com.tehreh1uneh.cloudstorage.common.messages.auth;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class AuthReq extends Message {

    private final String login;
    private final String password;

    public AuthReq(String login, String password) {
        super(MessageType.AUTH_REQ);
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
