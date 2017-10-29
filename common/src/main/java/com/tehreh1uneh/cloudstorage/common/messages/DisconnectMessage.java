package com.tehreh1uneh.cloudstorage.common.messages;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class DisconnectMessage extends Message {

    public DisconnectMessage() {
        super(MessageType.DISCONNECT);
    }
}

