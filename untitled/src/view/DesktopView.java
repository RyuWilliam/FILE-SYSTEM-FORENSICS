package view;

import controller.FileSystemManager;
import model.Folder;
import javax.swing.*;
import java.awt.*;

public class DesktopView extends JFrame {
    private JFrame parentFrame;
    
    public DesktopView(JFrame parent) {
        this.parentFrame = parent;
        
        setTitle("Escritorio");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Top Panel with path
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel pathLabel = new JLabel("C://Escritorio");
        pathLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pathLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(pathLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Folder button
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 50));
        
        JButton trabajosButton = new JButton("Trabajos");
        trabajosButton.setPreferredSize(new Dimension(150, 100));
        trabajosButton.setFont(new Font("Arial", Font.PLAIN, 16));
        trabajosButton.addActionListener(e -> openTrabajosFolder());
        centerPanel.add(trabajosButton);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel - Back Button
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> goBack());
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void openTrabajosFolder() {
        FolderContentView trabajosView = new FolderContentView(this, "Trabajos", "C://Escritorio/Trabajos");
        trabajosView.setVisible(true);
        this.setVisible(false);
    }
    
    private void goBack() {
        parentFrame.setVisible(true);
        this.dispose();
    }
}
