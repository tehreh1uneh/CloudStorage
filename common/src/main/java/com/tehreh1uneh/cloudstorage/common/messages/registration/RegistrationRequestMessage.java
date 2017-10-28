package com.tehreh1uneh.cloudstorage.common.messages.registration;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class RegistrationRequestMessage extends Message {

    private final String login;
    private final String password;

    public RegistrationRequestMessage(String login, String password) {
        super(MessageType.REG_REQUEST);
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

