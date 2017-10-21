package com.tehreh1uneh.cloudstorage.common.messages;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class ErrorMessage extends Message {

    private final String descrition;
    private final boolean disconnect;

    public ErrorMessage(String descrition, boolean disconnect) {
        super(MessageType.ERROR);
        this.descrition = descrition;
        this.disconnect = disconnect;
    }

    public String getDescrition() {
        return descrition;
    }

    public boolean isDisconnect() {
        return disconnect;
    }
}
