package view;

import javax.swing.*;
import java.awt.*;

public class DiskView extends JFrame {
    private JFrame parentFrame;
    
    public DiskView(JFrame parent) {
        this.parentFrame = parent;
        
        setTitle("Vista del Disco");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Carpetas del Disco", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Folders Panel
        JPanel foldersPanel = new JPanel();
        foldersPanel.setLayout(new GridLayout(2, 2, 20, 20));
        foldersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Path Label
        JPanel pathPanel = new JPanel();
        JLabel pathLabel = new JLabel("C://");
        pathLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pathPanel.add(pathLabel);
        titlePanel.add(pathLabel, BorderLayout.WEST);
        
        // Create 4 folder buttons
        String[] folderNames = {"Escritorio", "Documentos", "Fotos", "Música"};
        String[] folderPaths = {"C://Escritorio", "C://Documentos", "C://Fotos", "C://Música"};
        for (int i = 0; i < folderNames.length; i++) {
            String folderName = folderNames[i];
            String folderPath = folderPaths[i];
            JButton folderButton = new JButton(folderName);
            folderButton.setPreferredSize(new Dimension(150, 100));
            folderButton.setFont(new Font("Arial", Font.PLAIN, 16));
            
            if (folderName.equals("Escritorio")) {
                folderButton.addActionListener(e -> openDesktopView());
            } else {
                folderButton.addActionListener(e -> openFolderView(folderName, folderPath));
            }
            foldersPanel.add(folderButton);
        }
        
        add(foldersPanel, BorderLayout.CENTER);
        
        // Back Button Panel
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Volver al Menú");
        backButton.addActionListener(e -> goBack());
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void openDesktopView() {
        DesktopView desktopView = new DesktopView(this);
        desktopView.setVisible(true);
        this.setVisible(false);
    }
    
    private void openFolderView(String folderName, String folderPath) {
        FolderContentView folderView = new FolderContentView(this, folderName, folderPath);
        folderView.setVisible(true);
        this.setVisible(false);
    }
    
    private void goBack() {
        parentFrame.setVisible(true);
        this.dispose();
    }
}
