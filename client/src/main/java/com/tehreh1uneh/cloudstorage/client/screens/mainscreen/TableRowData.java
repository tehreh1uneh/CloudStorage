package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.tehreh1uneh.cloudstorage.client.screenmanager.Config.*;

public class TableRowData {

    private File file;
    private final String fileName;
    private ImageView icon;
    private String modified;
    private String type;
    private String size;

    TableRowData(File file) {
        this.file = file;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        modified = dateFormat.format(file.lastModified());

        if (file.isDirectory()) {
            fileName = file.getName();
            type = FOLDER_DESCRIPTION;
            setFolderIcon();
        } else {
            fileName = fileNameWithoutExt(file.getName());
            type = fileExt(file.getName());
            size = byteSizeToString(file.length());
        }
    }

    TableRowData() {
        setFolderIcon();
        fileName = "..";
    }

    private void setFolderIcon() {
        icon = new ImageView(new Image(ICON_FOLDER_PATH));
        icon.setFitWidth(COLUMN_FOLDER_WIDTH * ICON_FOLDER_RATIO);
        icon.setPreserveRatio(true);
    }

    public ImageView getIcon() {
        return icon;
    }

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

    public File getFile() {
        return file;
    }
}
