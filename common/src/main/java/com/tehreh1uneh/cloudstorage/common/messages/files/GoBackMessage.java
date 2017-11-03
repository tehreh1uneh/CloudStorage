package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class GoBackMessage extends Message {

    public GoBackMessage() {
        super(MessageType.GO_BACK);
    }
}
