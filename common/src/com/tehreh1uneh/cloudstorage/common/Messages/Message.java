package com.tehreh1uneh.cloudstorage.common.Messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected MessageType type = MessageType.ABSTRACT;

    public MessageType getType() {
        return type;
    }
}
