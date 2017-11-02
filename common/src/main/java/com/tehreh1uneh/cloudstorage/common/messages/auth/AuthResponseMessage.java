package com.tehreh1uneh.cloudstorage.common.messages.auth;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class AuthResponseMessage extends Message {

    private final boolean authorized;
    private final String message;

    public AuthResponseMessage(boolean authorized, String message) {
        super(MessageType.AUTH_RESPONSE);
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
