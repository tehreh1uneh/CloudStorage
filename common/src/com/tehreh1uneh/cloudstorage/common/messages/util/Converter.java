package com.tehreh1uneh.cloudstorage.common.messages.util;

import com.tehreh1uneh.cloudstorage.common.messages.Message;

import java.io.*;

public final class Converter {

    private ByteArrayOutputStream bos = new ByteArrayOutputStream();

    synchronized public byte[] objectToBytes(Serializable object) {
        try {
            bos.reset();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Невозможно преобразовать объект в массив байт");
        }
    }

    synchronized public Message bytesToMessage(byte[] bytes) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (Message) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Невозможно преобразовать массив байт в Message");
        }
    }

}
