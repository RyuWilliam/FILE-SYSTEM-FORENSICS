package view;

import controller.FileSystemController;
import controller.FileSystemManager;
import model.File;
import model.Folder;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FolderContentView extends JFrame {
    private JFrame parentFrame;
    private String folderName;
    private String folderPath;
    private FileSystemController controller;
    private Folder folder;
    private List<FileItemPanel> fileItemPanels;
    private JPanel filesPanel;
    
    public FolderContentView(JFrame parent, String folderName, String folderPath) {
        this.parentFrame = parent;
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.controller = FileSystemManager.getInstance().getController();
        this.fileItemPanels = new ArrayList<>();
        
        initializeFolder();
        setupUI();
    }
    
    private void initializeFolder() {
        // Get existing folder from manager
        folder = FileSystemManager.getInstance().getOrCreateFolder(folderPath, folderName);
    }
    
    private void setupUI() {
        setTitle("Contenido de " + folderName);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Top Panel with path
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel pathLabel = new JLabel(folderPath);
        pathLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pathLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(pathLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Files List
        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        filesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add file items (only non-deleted files)
        for (File file : folder.getFiles()) {
            // Skip files that have been deleted (null addresses)
            if (file.getMetadata().getLogicalAddress() != null && 
                file.getMetadata().getPhysicalAddress() != null) {
                FileItemPanel fileItemPanel = new FileItemPanel(file, this);
                fileItemPanels.add(fileItemPanel);
                filesPanel.add(fileItemPanel);
                filesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(filesPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // Right Panel - Action Buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton viewMetadataButton = new JButton("Ver Metadatos");
        viewMetadataButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewMetadataButton.addActionListener(e -> viewMetadata());
        
        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.addActionListener(e -> deleteFile());
        
        JButton backButton = new JButton("Volver");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> goBack());
        
        rightPanel.add(viewMetadataButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(deleteButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(backButton);
        
        add(rightPanel, BorderLayout.EAST);
    }
    
    private FileItemPanel getSelectedFilePanel() {
        for (FileItemPanel panel : fileItemPanels) {
            if (panel.isSelected()) {
                return panel;
            }
        }
        return null;
    }
    
    public void deselectOthers(FileItemPanel selectedPanel) {
        for (FileItemPanel panel : fileItemPanels) {
            if (panel != selectedPanel) {
                panel.setSelected(false);
            }
        }
    }
    
    private void viewMetadata() {
        FileItemPanel selectedPanel = getSelectedFilePanel();
        if (selectedPanel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo primero", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        MetadataDialog dialog = new MetadataDialog(this, selectedPanel.getFile());
        dialog.setVisible(true);
    }
    
    private void deleteFile() {
        FileItemPanel selectedPanel = getSelectedFilePanel();
        if (selectedPanel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo primero", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        File file = selectedPanel.getFile();
        
        // Show deletion process
        String message = "Eliminando archivo...\n\n" +
                "Liberando dirección lógica: " + file.getMetadata().getLogicalAddress() + "\n" +
                "Liberando dirección física: " + file.getMetadata().getPhysicalAddress() + "\n\n" +
                "NOTA: Los datos permanecen en el disco.";
        
        JOptionPane.showMessageDialog(this, message, 
            "Proceso de Eliminación", JOptionPane.INFORMATION_MESSAGE);
        
        // Delete file (free addresses but keep in folder for recovery)
        controller.deleteFile(folder, file);
        
        // Remove from UI only
        filesPanel.remove(selectedPanel);
        fileItemPanels.remove(selectedPanel);
        filesPanel.revalidate();
        filesPanel.repaint();
    }
    
    private void goBack() {
        parentFrame.setVisible(true);
        this.dispose();
    }
}
