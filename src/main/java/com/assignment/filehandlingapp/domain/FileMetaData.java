package com.assignment.filehandlingapp.domain;

public class FileMetaData {
    private String fileName;
    private String[] fileCategories;

    public FileMetaData(String fileName, String[] fileCategories) {
        this.fileName = fileName;
        this.fileCategories = fileCategories;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String[] getFileCategories() {
        return fileCategories;
    }

    public void setFileCategories(String[] fileCategories) {
        this.fileCategories = fileCategories;
    }
}
