package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class FileReq extends Message {

    private String fileName;

    public FileReq(String fileName) {
        super(MessageType.FILE_REQ);
        this.fileName = fileName;

    }

    public String getFileName() {
        return fileName;
    }
}
