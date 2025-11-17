package model;

public class FileItem {
    private File file;
    private boolean isDeleted;
    
    public FileItem(File file) {
        this.file = file;
        this.isDeleted = false;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    
    public String getFileName() {
        return file.getData().split("\\.")[0];
    }
}
