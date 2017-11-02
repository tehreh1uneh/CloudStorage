package com.tehreh1uneh.cloudstorage.common.messages.registration;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class RegistrationResponseMessage extends Message {

    private final boolean registered;
    private final String message;

    public RegistrationResponseMessage(boolean registered, String message) {
        super(MessageType.REG_RESPONSE);
        this.registered = registered;
        this.message = message;
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getMessage() {
        return message;
    }
}
