package view;

import controller.FileSystemController;
import controller.FileSystemManager;
import model.File;
import model.Folder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SystemTableView extends JFrame {
    private JFrame parentFrame;
    private FileSystemController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    
    public SystemTableView(JFrame parent) {
        this.parentFrame = parent;
        this.controller = FileSystemManager.getInstance().getController();
        
        setTitle("Tabla del Sistema");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Mapeo de Bloques Lógicos a Sectores Físicos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Bloque Lógico", "Sector Físico", "Pertenece al Archivo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Load test data
        loadTestData();
        
        // Bottom Panel with Back Button
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Volver al Menú");
        backButton.addActionListener(e -> goBack());
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadTestData() {
        // Make sure folders are initialized
        FileSystemManager.getInstance().initializeFolders();
        
        // Get existing folders from manager
        Folder trabajos = FileSystemManager.getInstance().getOrCreateFolder("C://Escritorio/Trabajos", "Trabajos");
        Folder documents = FileSystemManager.getInstance().getOrCreateFolder("C://Documentos", "Documentos");
        Folder photos = FileSystemManager.getInstance().getOrCreateFolder("C://Fotos", "Fotos");
        Folder music = FileSystemManager.getInstance().getOrCreateFolder("C://Música", "Música");
        
        // Populate table
        List<Folder> allFolders = new ArrayList<>();
        allFolders.add(trabajos);
        allFolders.add(documents);
        allFolders.add(photos);
        allFolders.add(music);
        
        // Map to track which logical blocks are used and by which file
        String[] blockOwner = new String[20]; // Total blocks in system
        
        // Initialize all as free
        for (int i = 0; i < blockOwner.length; i++) {
            blockOwner[i] = "[LIBRE]";
        }
        
        // Mark blocks used by files
        for (Folder folder : allFolders) {
            for (File file : folder.getFiles()) {
                Integer logicalBlock = file.getMetadata().getLogicalAddress();
                Integer physicalSector = file.getMetadata().getPhysicalAddress();
                
                if (logicalBlock != null && physicalSector != null) {
                    int blocksUsed = (int) Math.ceil((double) file.getMetadata().getSize() / 4096);
                    
                    for (int i = 0; i < blocksUsed; i++) {
                        int currentLogicalBlock = logicalBlock + i;
                        blockOwner[currentLogicalBlock] = file.getData();
                    }
                }
            }
        }
        
        // Add all blocks in order (0 to 19)
        for (int i = 0; i < blockOwner.length; i++) {
            int physicalStart = i * 8;
            Object[] row = {
                i,
                physicalStart + " - " + (physicalStart + 7),
                blockOwner[i]
            };
            tableModel.addRow(row);
        }
        
        // If no data, show message
        if (tableModel.getRowCount() == 0) {
            Object[] emptyRow = {"-", "-", "Sin archivos en el sistema"};
            tableModel.addRow(emptyRow);
        }
    }
    
    private void goBack() {
        parentFrame.setVisible(true);
        this.dispose();
    }
}
