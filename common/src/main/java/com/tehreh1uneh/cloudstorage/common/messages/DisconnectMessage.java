package com.tehreh1uneh.cloudstorage.common.messages;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class DisconnectMessage extends Message {

    private String message;

    public DisconnectMessage(String message) {
        super(MessageType.DISCONNECT);
        this.message = message;
    }
}

