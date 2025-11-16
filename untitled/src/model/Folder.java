package model;

import java.util.ArrayList;
import java.util.List;

public class Folder {
    private List<Folder> folders;
    private List<File> files;
    private String path;

    public Folder(String path) {
        this.path = path;
        this.folders = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addFolder(Folder folder) {
        this.folders.add(folder);
    }

    public void addFile(File file) {
        this.files.add(file);
    }
}
