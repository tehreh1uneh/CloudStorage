package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

public class TableRowData {

    private String fileName;
    private String modified;
    private String type;
    private String size;

    public TableRowData(String fileName, String modified, String type, String size) {
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

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
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
