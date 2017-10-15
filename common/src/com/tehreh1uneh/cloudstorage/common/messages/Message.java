package com.tehreh1uneh.cloudstorage.common.messages;

import java.io.Serializable;

public class Message implements Serializable {

    @SuppressWarnings("WeakerAccess")
    protected MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }
}
