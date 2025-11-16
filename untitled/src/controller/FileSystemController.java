package controller;

import model.File;
import model.Folder;
import model.Metadata;

import java.time.LocalDateTime;
import java.util.Arrays;

public class FileSystemController {
    private static final int PHYSICAL_SECTOR_SIZE = 512; // bytes
    private static final int LOGICAL_BLOCK_SIZE = 4096; // 4KB
    private static final int SECTORS_PER_BLOCK = LOGICAL_BLOCK_SIZE / PHYSICAL_SECTOR_SIZE; // 8 sectors
    private static final int TOTAL_LOGICAL_BLOCKS = 20; // Small disk: 20 blocks = 80KB
    private static final int TOTAL_PHYSICAL_SECTORS = TOTAL_LOGICAL_BLOCKS * SECTORS_PER_BLOCK; // 160 sectors
    
    private boolean[] logicalBlockMap; // true = occupied, false = free
    private boolean[] physicalSectorMap; // true = occupied, false = free
    
    public FileSystemController() {
        logicalBlockMap = new boolean[TOTAL_LOGICAL_BLOCKS];
        physicalSectorMap = new boolean[TOTAL_PHYSICAL_SECTORS];
        Arrays.fill(logicalBlockMap, false);
        Arrays.fill(physicalSectorMap, false);
    }
    
    public File createFile(String data, int size) {
        Metadata metadata = new Metadata(LocalDateTime.now(), size, null, null);
        return new File(data, metadata);
    }
    
    public Folder createFolder(String path) {
        return new Folder(path);
    }
    
    public void addFileToFolder(Folder folder, File file) {
        // Calculate required logical blocks
        int requiredBlocks = (int) Math.ceil((double) file.getMetadata().getSize() / LOGICAL_BLOCK_SIZE);
        
        if (requiredBlocks == 0) requiredBlocks = 1; // Minimum 1 block
        
        // Find first available logical block
        int logicalBlockStart = findConsecutiveFreeBlocks(requiredBlocks);
        if (logicalBlockStart == -1) {
            System.out.println("ERROR: Not enough space in disk for file of size " + file.getMetadata().getSize());
            return;
        }
        
        // Calculate physical sectors needed
        int sectorsNeeded = requiredBlocks * SECTORS_PER_BLOCK;
        int physicalSectorStart = logicalBlockStart * SECTORS_PER_BLOCK;
        
        // Mark blocks and sectors as occupied
        for (int i = 0; i < requiredBlocks; i++) {
            logicalBlockMap[logicalBlockStart + i] = true;
        }
        for (int i = 0; i < sectorsNeeded; i++) {
            physicalSectorMap[physicalSectorStart + i] = true;
        }
        
        // Assign addresses
        file.getMetadata().setLogicalAddress(logicalBlockStart);
        file.getMetadata().setPhysicalAddress(physicalSectorStart);
        
        folder.addFile(file);
    }
    
    private int findConsecutiveFreeBlocks(int requiredBlocks) {
        int consecutiveCount = 0;
        for (int i = 0; i < TOTAL_LOGICAL_BLOCKS; i++) {
            if (!logicalBlockMap[i]) {
                consecutiveCount++;
                if (consecutiveCount == requiredBlocks) {
                    return i - requiredBlocks + 1; // Return start position
                }
            } else {
                consecutiveCount = 0;
            }
        }
        return -1; // Not enough space
    }
    
    public void addSubfolder(Folder parentFolder, Folder subfolder) {
        parentFolder.addFolder(subfolder);
    }
    
    public Metadata createMetadata(int size, Integer logicalAddress, Integer physicalAddress) {
        return new Metadata(LocalDateTime.now(), size, logicalAddress, physicalAddress);
    }
    
    public void printDiskStatus() {
        System.out.println("\n=== DISK STATUS ===");
        System.out.println("Total logical blocks: " + TOTAL_LOGICAL_BLOCKS + " (Total capacity: " + (TOTAL_LOGICAL_BLOCKS * LOGICAL_BLOCK_SIZE / 1024) + " KB)");
        System.out.println("Total physical sectors: " + TOTAL_PHYSICAL_SECTORS);
        
        int usedBlocks = 0;
        for (boolean occupied : logicalBlockMap) {
            if (occupied) usedBlocks++;
        }
        System.out.println("Used blocks: " + usedBlocks + " / " + TOTAL_LOGICAL_BLOCKS);
        System.out.println("Free blocks: " + (TOTAL_LOGICAL_BLOCKS - usedBlocks));
        
        System.out.print("Block map: [");
        for (int i = 0; i < TOTAL_LOGICAL_BLOCKS; i++) {
            System.out.print(logicalBlockMap[i] ? "X" : ".");
        }
        System.out.println("]");
    }
    
    public void deleteFile(Folder folder, File file) {
        Integer logicalBlock = file.getMetadata().getLogicalAddress();
        Integer physicalSector = file.getMetadata().getPhysicalAddress();
        
        if (logicalBlock != null && physicalSector != null) {
            int blocksUsed = (int) Math.ceil((double) file.getMetadata().getSize() / LOGICAL_BLOCK_SIZE);
            if (blocksUsed == 0) blocksUsed = 1;
            
            // Free logical blocks
            for (int i = 0; i < blocksUsed; i++) {
                if (logicalBlock + i < TOTAL_LOGICAL_BLOCKS) {
                    logicalBlockMap[logicalBlock + i] = false;
                }
            }
            
            // Free physical sectors
            int sectorsUsed = blocksUsed * SECTORS_PER_BLOCK;
            for (int i = 0; i < sectorsUsed; i++) {
                if (physicalSector + i < TOTAL_PHYSICAL_SECTORS) {
                    physicalSectorMap[physicalSector + i] = false;
                }
            }
            
            // Mark addresses as null but keep the file data for recovery
            file.getMetadata().setLogicalAddress(null);
            file.getMetadata().setPhysicalAddress(null);
        }
    }
    
    public boolean recoverFile(File file) {
        // Calculate required blocks
        int requiredBlocks = (int) Math.ceil((double) file.getMetadata().getSize() / LOGICAL_BLOCK_SIZE);
        if (requiredBlocks == 0) requiredBlocks = 1;
        
        // Find space for the file
        int logicalBlockStart = findConsecutiveFreeBlocks(requiredBlocks);
        if (logicalBlockStart == -1) {
            return false; // Not enough space
        }
        
        // Calculate physical sectors
        int sectorsNeeded = requiredBlocks * SECTORS_PER_BLOCK;
        int physicalSectorStart = logicalBlockStart * SECTORS_PER_BLOCK;
        
        // Mark blocks and sectors as occupied
        for (int i = 0; i < requiredBlocks; i++) {
            logicalBlockMap[logicalBlockStart + i] = true;
        }
        for (int i = 0; i < sectorsNeeded; i++) {
            physicalSectorMap[physicalSectorStart + i] = true;
        }
        
        // Assign new addresses
        file.getMetadata().setLogicalAddress(logicalBlockStart);
        file.getMetadata().setPhysicalAddress(physicalSectorStart);
        
        return true;
    }
}
