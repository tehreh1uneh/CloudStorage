package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import java.io.File;
import java.text.SimpleDateFormat;

public class TableRowData {

    private File file;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private String fileName;
    private String modified;
    private String type;
    private String size;

    public String getFileName() {
        return fileName;
    }

    public String getModified() {
        return modified;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    TableRowData(File file) {
        this.file = file;

        this.fileName = fileNameWithoutExt(file.getName());
        this.modified = dateFormat.format(file.lastModified());
        this.type = fileExt(file.getName());
        this.size = byteSizeToString(file.length());
    }

    private String byteSizeToString(long length) {
        if (length < 1024) {
            return Long.toString(length) + " байт";
        } else if (length < 1024 * 1024) {
            long left = length / 1024;
            int right = (int) ((double) (length % 1024) / 1024 * 100);

            return Long.toString(left) + "." + Integer.toString(right) + " КБ";
        } else {
            long left = length / (1024 * 1024);
            int right = (int) ((double) (length % (1024 * 1024)) / (1024 * 1024) * 100);

            return Long.toString(left) + "." + Integer.toString(right) + " МБ";
        }
    }

    private String fileExt(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private String fileNameWithoutExt(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }

    }

    File getFile() {
        return file;
    }
}
