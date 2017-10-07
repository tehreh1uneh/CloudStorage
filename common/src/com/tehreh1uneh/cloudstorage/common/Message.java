package com.tehreh1uneh.cloudstorage.common;

import java.io.Serializable;

public abstract class Message implements Serializable {
    protected MessageType type = MessageType.ABSTRACT;

}
