package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

public class FilesListReq extends Message {

    public FilesListReq() {
        super(MessageType.FILES_LIST_REQ);
    }
}
