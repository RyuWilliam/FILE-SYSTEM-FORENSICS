package view;

import model.File;
import javax.swing.*;
import java.awt.*;

public class MetadataDialog extends JDialog {
    
    public MetadataDialog(JFrame parent, File file) {
        super(parent, "Metadatos del Archivo", true);
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(6, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // File name
        contentPanel.add(new JLabel("Nombre:"));
        contentPanel.add(new JLabel(file.getData()));
        
        // Creation date
        contentPanel.add(new JLabel("Fecha de creación:"));
        contentPanel.add(new JLabel(file.getMetadata().getCreationDate().toString()));
        
        // Size
        contentPanel.add(new JLabel("Tamaño:"));
        contentPanel.add(new JLabel(file.getMetadata().getSize() + " bytes"));
        
        // Logical address
        contentPanel.add(new JLabel("Dirección lógica:"));
        Integer logicalAddr = file.getMetadata().getLogicalAddress();
        contentPanel.add(new JLabel(logicalAddr != null ? logicalAddr.toString() : "No asignada"));
        
        // Physical address
        contentPanel.add(new JLabel("Dirección física:"));
        Integer physicalAddr = file.getMetadata().getPhysicalAddress();
        contentPanel.add(new JLabel(physicalAddr != null ? physicalAddr.toString() : "No asignada"));
        
        // Blocks used
        contentPanel.add(new JLabel("Bloques usados:"));
        int blocks = (int) Math.ceil((double) file.getMetadata().getSize() / 4096);
        contentPanel.add(new JLabel(String.valueOf(blocks)));
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Back Button
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> dispose());
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
