package view;

import model.File;
import javax.swing.*;
import java.awt.*;

public class FileItemPanel extends JPanel {
    private File file;
    private JCheckBox checkbox;
    private JLabel nameLabel;
    private FolderContentView parentView;
    private boolean deleted;
    
    public FileItemPanel(File file, FolderContentView parentView) {
        this.file = file;
        this.parentView = parentView;
        this.deleted = false;
        
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        checkbox = new JCheckBox();
        checkbox.addActionListener(e -> {
            if (checkbox.isSelected()) {
                parentView.deselectOthers(this);
            }
        });
        
        nameLabel = new JLabel(file.getData());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        add(checkbox);
        add(nameLabel);
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
    
    public void markAsDeleted() {
        deleted = true;
        nameLabel.setForeground(Color.GRAY);
        nameLabel.setText(file.getData() + " (eliminado)");
        checkbox.setEnabled(false);
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
