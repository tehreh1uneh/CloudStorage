package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import java.time.LocalDateTime;

public class TableRowData {

    private String fileName;
    private LocalDateTime modified;
    private String type;
    private String size;

    public TableRowData(String fileName, LocalDateTime modified, String type, String size) {
        this.fileName = fileName;
        this.modified = modified;
        this.type = type;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
