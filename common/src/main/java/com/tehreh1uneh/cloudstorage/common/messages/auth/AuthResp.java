package com.tehreh1uneh.cloudstorage.common.messages.auth;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class AuthResp extends Message {

    private boolean authorized;
    private String message;

    public AuthResp(boolean authorized, String message) {
        super(MessageType.AUTH_RESP);
        this.authorized = authorized;
        this.message = message;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public String getMessage() {
        return message;
    }

}
