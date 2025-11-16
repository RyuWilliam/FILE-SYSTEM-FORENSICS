package view;

import model.File;
import javax.swing.*;
import java.awt.*;

public class DeletedFilePanel extends JPanel {
    private File file;
    private JCheckBox checkbox;
    private JLabel nameLabel;
    private JLabel dateLabel;
    private RecoverFilesView parentView;
    
    public DeletedFilePanel(File file, RecoverFilesView parentView) {
        this.file = file;
        this.parentView = parentView;
        
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        checkbox = new JCheckBox();
        checkbox.addActionListener(e -> {
            if (checkbox.isSelected()) {
                parentView.deselectOthers(this);
            }
        });
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        nameLabel = new JLabel("Archivo: " + file.getData());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        dateLabel = new JLabel("Fecha de creación: " + file.getMetadata().getCreationDate().toString());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        dateLabel.setForeground(Color.GRAY);
        
        infoPanel.add(nameLabel);
        infoPanel.add(dateLabel);
        
        add(checkbox, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
    }
    
    public boolean isSelected() {
        return checkbox.isSelected();
    }
    
    public void setSelected(boolean selected) {
        checkbox.setSelected(selected);
    }
    
    public File getFile() {
        return file;
    }
}
