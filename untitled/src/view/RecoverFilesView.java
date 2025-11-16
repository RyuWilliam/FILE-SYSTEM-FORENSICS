package view;

import controller.FileSystemController;
import controller.FileSystemManager;
import model.File;
import model.Folder;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecoverFilesView extends JFrame {
    private JFrame parentFrame;
    private FileSystemController controller;
    private List<DeletedFilePanel> deletedFilePanels;
    private JPanel filesPanel;
    private List<File> deletedFiles;
    
    public RecoverFilesView(JFrame parent) {
        this.parentFrame = parent;
        this.controller = FileSystemManager.getInstance().getController();
        this.deletedFilePanels = new ArrayList<>();
        this.deletedFiles = new ArrayList<>();
        
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Recuperar Archivos");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Top Panel with title
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Recuperación de Archivos Eliminados");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Deleted Files List
        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        filesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel emptyLabel = new JLabel("Haga clic en 'Escanear' para buscar archivos eliminados");
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        filesPanel.add(emptyLabel);
        
        JScrollPane scrollPane = new JScrollPane(filesPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // Right Panel - Action Buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton scanButton = new JButton("Escanear");
        scanButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scanButton.setPreferredSize(new Dimension(120, 40));
        scanButton.addActionListener(e -> scanDeletedFiles());
        
        JButton recoverButton = new JButton("Recuperar");
        recoverButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        recoverButton.setPreferredSize(new Dimension(120, 40));
        recoverButton.addActionListener(e -> recoverSelectedFile());
        
        JButton backButton = new JButton("Volver");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> goBack());
        
        rightPanel.add(scanButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(recoverButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(backButton);
        
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void scanDeletedFiles() {
        // Clear previous scan
        filesPanel.removeAll();
        deletedFilePanels.clear();
        deletedFiles.clear();
        
        // Get all folders and find deleted files
        FileSystemManager manager = FileSystemManager.getInstance();
        List<String> folderPaths = List.of(
            "C://Escritorio/Trabajos",
            "C://Documentos",
            "C://Fotos",
            "C://Música"
        );
        
        for (String path : folderPaths) {
            Folder folder = manager.getOrCreateFolder(path, "");
            for (File file : folder.getFiles()) {
                // File is deleted if it has null addresses but has data
                if (file.getMetadata().getLogicalAddress() == null && 
                    file.getMetadata().getPhysicalAddress() == null) {
                    deletedFiles.add(file);
                }
            }
        }
        
        if (deletedFiles.isEmpty()) {
            JLabel noFilesLabel = new JLabel("No se encontraron archivos eliminados");
            noFilesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            filesPanel.add(noFilesLabel);
        } else {
            for (File file : deletedFiles) {
                DeletedFilePanel filePanel = new DeletedFilePanel(file, this);
                deletedFilePanels.add(filePanel);
                filesPanel.add(filePanel);
                filesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        filesPanel.revalidate();
        filesPanel.repaint();
        
        JOptionPane.showMessageDialog(this, 
            "Escaneo completado. Se encontraron " + deletedFiles.size() + " archivo(s) eliminado(s).",
            "Resultado del Escaneo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private DeletedFilePanel getSelectedFilePanel() {
        for (DeletedFilePanel panel : deletedFilePanels) {
            if (panel.isSelected()) {
                return panel;
            }
        }
        return null;
    }
    
    public void deselectOthers(DeletedFilePanel selectedPanel) {
        for (DeletedFilePanel panel : deletedFilePanels) {
            if (panel != selectedPanel) {
                panel.setSelected(false);
            }
        }
    }
    
    private void recoverSelectedFile() {
        DeletedFilePanel selectedPanel = getSelectedFilePanel();
        if (selectedPanel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo para recuperar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        File file = selectedPanel.getFile();
        
        // Try to recover by finding space in disk
        boolean recovered = controller.recoverFile(file);
        
        if (recovered) {
            String message = "Archivo recuperado exitosamente:\n\n" +
                    "Nombre: " + file.getData() + "\n" +
                    "Nueva dirección lógica: " + file.getMetadata().getLogicalAddress() + "\n" +
                    "Nueva dirección física: " + file.getMetadata().getPhysicalAddress();
            
            JOptionPane.showMessageDialog(this, message, 
                "Recuperación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            
            // Remove from deleted list
            deletedFiles.remove(file);
            deletedFilePanels.remove(selectedPanel);
            filesPanel.remove(selectedPanel);
            
            if (deletedFiles.isEmpty()) {
                filesPanel.removeAll();
                JLabel noFilesLabel = new JLabel("No hay más archivos eliminados");
                noFilesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                filesPanel.add(noFilesLabel);
            }
            
            filesPanel.revalidate();
            filesPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, 
                "No hay espacio suficiente en el disco para recuperar este archivo",
                "Error de Recuperación", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void goBack() {
        parentFrame.setVisible(true);
        this.dispose();
    }
}
