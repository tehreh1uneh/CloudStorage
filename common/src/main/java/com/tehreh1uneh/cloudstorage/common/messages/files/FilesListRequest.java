package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

import java.io.File;

public class FilesListRequest extends Message {

    private boolean directory;
    private String directoryPath;

    public FilesListRequest(boolean directory, File file) {
        super(MessageType.FILES_LIST_REQUEST);
        this.directory = directory;
        directoryPath = file.getPath();
    }

    public boolean isDirectory() {
        return directory;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

}
