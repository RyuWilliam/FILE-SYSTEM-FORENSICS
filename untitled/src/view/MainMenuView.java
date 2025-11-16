package view;

import controller.FileSystemManager;
import javax.swing.*;
import java.awt.*;

public class MainMenuView extends JFrame {
    
    public MainMenuView() {
        setTitle("Simulador de Sistema de Archivos");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Simulador de Sistema de Archivos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(titleLabel, gbc);
        
        // View Disk Button
        JButton viewDiskButton = new JButton("Ver Disco");
        viewDiskButton.setPreferredSize(new Dimension(200, 40));
        viewDiskButton.addActionListener(e -> openDiskView());
        gbc.gridy = 1;
        add(viewDiskButton, gbc);
        
        // Recover Files Button
        JButton recoverFilesButton = new JButton("Recuperar Archivos");
        recoverFilesButton.setPreferredSize(new Dimension(200, 40));
        recoverFilesButton.addActionListener(e -> openRecoverFiles());
        gbc.gridy = 2;
        add(recoverFilesButton, gbc);
        
        // View System Table Button
        JButton viewTableButton = new JButton("Ver Tabla del Sistema");
        viewTableButton.setPreferredSize(new Dimension(200, 40));
        viewTableButton.addActionListener(e -> openSystemTable());
        gbc.gridy = 3;
        add(viewTableButton, gbc);
        
        // Exit Button
        JButton exitButton = new JButton("Salir");
        exitButton.setPreferredSize(new Dimension(200, 40));
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 4;
        add(exitButton, gbc);
    }
    
    private void openDiskView() {
        // Initialize folders on first access
        FileSystemManager.getInstance().initializeFolders();
        DiskView diskView = new DiskView(this);
        diskView.setVisible(true);
        this.setVisible(false);
    }
    
    private void openRecoverFiles() {
        FileSystemManager.getInstance().initializeFolders();
        RecoverFilesView recoverView = new RecoverFilesView(this);
        recoverView.setVisible(true);
        this.setVisible(false);
    }
    
    private void openSystemTable() {
        SystemTableView tableView = new SystemTableView(this);
        tableView.setVisible(true);
        this.setVisible(false);
    }
}
