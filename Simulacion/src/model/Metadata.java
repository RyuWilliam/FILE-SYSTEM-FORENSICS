package model;

import java.time.LocalDateTime;

public class Metadata {
    private LocalDateTime creationDate;
    private int size;
    private Integer logicalAddress;
    private Integer physicalAddress;

    public Metadata(LocalDateTime creationDate, int size, Integer logicalAddress, Integer physicalAddress) {
        this.creationDate = creationDate;
        this.size = size;
        this.logicalAddress = logicalAddress;
        this.physicalAddress = physicalAddress;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer getLogicalAddress() {
        return logicalAddress;
    }

    public void setLogicalAddress(Integer logicalAddress) {
        this.logicalAddress = logicalAddress;
    }

    public Integer getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(Integer physicalAddress) {
        this.physicalAddress = physicalAddress;
    }
}
