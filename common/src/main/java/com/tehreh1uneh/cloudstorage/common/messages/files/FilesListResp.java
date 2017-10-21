package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

import java.io.File;
import java.util.ArrayList;

public class FilesListResp extends Message {

    private ArrayList<File> filesList;

    public FilesListResp(ArrayList<File> filesList) {
        super(MessageType.FILES_LIST_RESP);
        this.filesList = filesList;
    }

    public ArrayList<File> getFilesList() {
        return filesList;
    }
}
