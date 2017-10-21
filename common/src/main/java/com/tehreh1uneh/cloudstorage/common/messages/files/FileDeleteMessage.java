package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class FileDeleteMessage extends Message {

    private String fileName;

    public FileDeleteMessage(String fileName) {
        super(MessageType.FILE_DELETE);
        this.fileName = fileName;

    }

    public String getFileName() {
        return fileName;
    }
}
