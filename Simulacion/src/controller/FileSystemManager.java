package controller;

import model.Folder;
import model.File;
import java.util.HashMap;
import java.util.Map;

public class FileSystemManager {
    private static FileSystemManager instance;
    private FileSystemController controller;
    private Map<String, Folder> folders; // Store folders by path
    private boolean initialized = false;
    
    private FileSystemManager() {
        controller = new FileSystemController();
        folders = new HashMap<>();
    }
    
    public static FileSystemManager getInstance() {
        if (instance == null) {
            instance = new FileSystemManager();
        }
        return instance;
    }
    
    public FileSystemController getController() {
        return controller;
    }
    
    public Folder getOrCreateFolder(String path, String folderName) {
        if (folders.containsKey(path)) {
            return folders.get(path);
        }
        
        // Create new folder (only for Desktop or if called directly)
        Folder newFolder = controller.createFolder(path);
        folders.put(path, newFolder);
        return newFolder;
    }
    
    public void initializeFolders() {
        if (!initialized) {
            // Desktop - empty main folder
            Folder desktop = controller.createFolder("C://Escritorio");
            folders.put("C://Escritorio", desktop);
            
            // Trabajos subfolder inside Desktop
            Folder trabajos = controller.createFolder("C://Escritorio/Trabajos");
            File trabajoFile = controller.createFile("trabajo.docx", 3500);
            controller.addFileToFolder(trabajos, trabajoFile);
            folders.put("C://Escritorio/Trabajos", trabajos);
            
            // Documents - small text files
            Folder docs = controller.createFolder("C://Documentos");
            File docFile1 = controller.createFile("informe.txt", 800);
            File docFile2 = controller.createFile("notas.doc", 1200);
            controller.addFileToFolder(docs, docFile1);
            controller.addFileToFolder(docs, docFile2);
            folders.put("C://Documentos", docs);
            
            // Photos - larger files
            Folder photos = controller.createFolder("C://Fotos");
            File photoFile1 = controller.createFile("vacaciones.jpg", 7500);
            File photoFile2 = controller.createFile("familia.png", 9200);
            controller.addFileToFolder(photos, photoFile1);
            controller.addFileToFolder(photos, photoFile2);
            folders.put("C://Fotos", photos);
            
            // Music - largest files
            Folder music = controller.createFolder("C://Música");
            File musicFile1 = controller.createFile("cancion1.mp3", 12000);
            File musicFile2 = controller.createFile("cancion2.mp3", 15500);
            controller.addFileToFolder(music, musicFile1);
            controller.addFileToFolder(music, musicFile2);
            folders.put("C://Música", music);
            
            initialized = true;
        }
    }
}
