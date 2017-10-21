package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class FileRequestMessage extends Message {

    private String fileName;

    public FileRequestMessage(String fileName) {
        super(MessageType.FILE_REQUEST);
        this.fileName = fileName;

    }

    public String getFileName() {
        return fileName;
    }
}
