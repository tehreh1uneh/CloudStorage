package com.tehreh1uneh.cloudstorage.common.messages.base;

import java.io.Serializable;

public class Message implements Serializable {

    @SuppressWarnings("WeakerAccess")
    protected final MessageType type;

    protected Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }
}
