package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class FileDel extends Message {

    private String fileName;

    public FileDel(String fileName) {
        super(MessageType.FILE_DEL);
        this.fileName = fileName;

    }

    public String getFileName() {
        return fileName;
    }
}
