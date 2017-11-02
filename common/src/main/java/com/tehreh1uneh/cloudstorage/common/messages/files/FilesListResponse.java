package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

import java.io.File;
import java.util.ArrayList;

public class FilesListResponse extends Message {

    private final ArrayList<File> filesList;
    private final boolean root;

    public FilesListResponse(ArrayList<File> filesList, boolean root) {
        super(MessageType.FILES_LIST_RESPONSE);
        this.filesList = filesList;
        this.root = root;
    }

    public boolean isRoot() {
        return root;
    }

    public ArrayList<File> getFilesList() {
        return filesList;
    }
}
