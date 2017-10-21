package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

import java.io.File;
import java.util.ArrayList;

public class FilesListResponse extends Message {

    private ArrayList<File> filesList;

    public FilesListResponse(ArrayList<File> filesList) {
        super(MessageType.FILES_LIST_RESPONSE);
        this.filesList = filesList;
    }

    public ArrayList<File> getFilesList() {
        return filesList;
    }
}
