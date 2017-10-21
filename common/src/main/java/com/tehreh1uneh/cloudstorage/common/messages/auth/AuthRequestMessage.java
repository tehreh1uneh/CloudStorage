package com.tehreh1uneh.cloudstorage.common.messages.auth;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class AuthRequestMessage extends Message {

    private final String login;
    private final String password;

    public AuthRequestMessage(String login, String password) {
        super(MessageType.AUTH_REQUEST);
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
