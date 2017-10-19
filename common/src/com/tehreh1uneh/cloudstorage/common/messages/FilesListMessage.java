package com.tehreh1uneh.cloudstorage.common.messages;

import java.io.File;
import java.util.ArrayList;

public class FilesListMessage extends Message {

    private ArrayList<File> filesList;

    public FilesListMessage(ArrayList<File> filesList) {
        super(MessageType.FILES_LIST);
        this.filesList = filesList;
    }

    public ArrayList<File> getFilesList() {
        return filesList;
    }
}
