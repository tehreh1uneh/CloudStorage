package com.tehreh1uneh.cloudstorage.common.Messages;

public class AuthRequestMessage extends Message {

    private final String login;
    private final String password;

    {
        type = MessageType.AUTH_REQUEST;
    }

    public AuthRequestMessage(String login, String password) {
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
