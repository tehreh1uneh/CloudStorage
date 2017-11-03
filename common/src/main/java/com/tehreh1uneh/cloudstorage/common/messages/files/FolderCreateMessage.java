package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class FolderCreateMessage extends Message {

    private final String name;

    public FolderCreateMessage(String name) {
        super(MessageType.FOLDER_CREATE);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
