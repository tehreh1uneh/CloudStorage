package com.tehreh1uneh.cloudstorage.common.messages.files;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileMessage extends Message {

    private final String name;
    private final byte[] bytes;

    public FileMessage(File file) {
        super(MessageType.FILE);
        name = file.getName();
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
