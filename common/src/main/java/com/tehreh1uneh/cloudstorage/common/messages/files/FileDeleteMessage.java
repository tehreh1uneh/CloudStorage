package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

import java.io.File;

public class FileDeleteMessage extends Message {

    private final File file;

    public FileDeleteMessage(File file) {
        super(MessageType.FILE_DELETE);
        this.file = file;

    }

    public File getFile() {
        return file;
    }
}
