package model;

public class File {
    private String data;
    private Metadata metadata;

    public File(String data, Metadata metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
