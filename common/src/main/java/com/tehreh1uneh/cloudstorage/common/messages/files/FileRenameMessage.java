package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

import java.io.File;

public class FileRenameMessage extends Message {

    private final File file;
    private final String newName;

    public FileRenameMessage(File file, String newName) {
        super(MessageType.FILE_RENAME);
        this.file = file;
        this.newName = newName;
    }

    public File getFile() {
        return file;
    }

    public String getNewName() {
        return newName;
    }
}
