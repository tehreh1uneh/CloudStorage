package com.tehreh1uneh.cloudstorage.common.messages.base;

public enum MessageType {
    AUTH_REQUEST, AUTH_RESPONSE,
    FILE_REQUEST, FILE,
    FILE_DELETE, FILE_RENAME,
    FOLDER_CREATE,
    GO_BACK,
    FILES_LIST_REQUEST, FILES_LIST_RESPONSE,
    REG_REQUEST, REG_RESPONSE,
    ERROR, DISCONNECT
}
