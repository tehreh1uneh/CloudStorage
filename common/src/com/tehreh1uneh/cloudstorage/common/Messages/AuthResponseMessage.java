package com.tehreh1uneh.cloudstorage.common.Messages;

public class AuthResponseMessage extends Message {

    private boolean authorized;
    private String message;

    {
        type = MessageType.AUTH_RESPONSE;
    }

    public AuthResponseMessage(boolean authorized, String message) {
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
