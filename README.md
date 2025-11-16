# Simulador de Sistema de Archivos con Análisis Forense

Simulador educativo de un sistema de archivos con conceptos de análisis forense digital. Desarrollado en Java con interfaz Swing.

## Cómo Ejecutar

### Requisitos
- Java JDK 11 o superior

### Desde línea de comandos
```bash
# Compilar
javac -d bin src/**/*.java

# Ejecutar
java -cp bin Main
```

### Desde IDE
Ejecutar directamente `Main.java` desde IntelliJ IDEA, Eclipse o VS Code.

---

## Conceptos Implementados

### 4.1 Conceptos Fundamentales

- **Archivo**: Unidad de información con metadatos (nombre, tamaño, fechas, direcciones)
- **Bloque Lógico**: 4KB, unidad mínima de almacenamiento
- **Sector Físico**: 512 bytes, cada bloque contiene 8 sectores
- **Metadatos (Inodo)**: Fecha de creación, tamaño, dirección lógica/física
- **Directorio**: Estructura jerárquica que asocia nombres con metadatos
- **Slack Space**: Espacio no utilizado dentro de bloques asignados

### 4.2 Análisis Forense

- **Recuperación**: Escaneo de archivos con metadatos intactos pero direcciones liberadas
- **Reconstrucción**: Preservación de timestamps, nombres y tamaños tras "borrado"
- **Análisis de Estructuras**: Visualización de tabla de direcciones y mapa de ocupación
- **Timeline**: Timestamps para reconstruir secuencia temporal de operaciones

### 4.3 Proceso de Borrado
1. **Desvinculación**: Archivo se remueve de la vista pero permanece en memoria
2. **Marcado del Inodo**: Direcciones se marcan como null (libre)
3. **Liberación de Bloques**: Bitmaps actualizados pero datos NO se sobrescriben
4. **Persistencia**: Metadatos completos se mantienen para recuperación

### 4.4 Restos Forenses

- **Bloques No Sobreescritos**: Bloques `[LIBRE]` pueden contener datos previos
- **Entradas Residuales**: Archivos eliminados persisten en estructura interna
- **Timestamps**: Fechas de creación y tamaños originales se preservan
- **Mapa de Asignación**: Tabla muestra bloques libres vs ocupados

### 4.5 Técnicas de Recuperación

- **Análisis Lógico**: Escaneo buscando archivos con direcciones null
- **Recuperación por Metadatos**: Usa tamaño original para calcular bloques necesarios
- **Reensamblado**: Asignación de nuevas direcciones y actualización de bitmaps

## Arquitectura (MVC)

- **Modelo**: `File`, `Folder`, `Metadata`, `FileItem`
- **Controlador**: `FileSystemController` (operaciones de archivos), `FileSystemManager` (singleton global)
- **Vista**: 9 clases Swing para navegación, exploración, tabla de sistema y recuperación

## Características Técnicas

- **Capacidad**: 20 bloques lógicos (80 KB total)
- **Bloque Lógico**: 4 KB (8 sectores físicos de 512 bytes)
- **Asignación**: Consecutiva (first-fit)
- **Estructura Inicial**: 4 carpetas principales (Escritorio, Documentos, Fotos, Música) con 7 archivos

## Funcionalidades

1. **Ver Disco**: Navegación de carpetas, visualización de archivos, consulta de metadatos y eliminación
2. **Tabla del Sistema**: Mapa completo de 20 bloques con estado (ocupado/libre) en tiempo real
3. **Recuperar Archivos**: Escaneo y recuperación de archivos eliminados con validación de espacio

## Propósito Educativo

Diseñado para demostrar:
- Funcionamiento interno de sistemas de archivos
- Diferencia entre borrado lógico y sobrescritura física
- Técnicas básicas de análisis forense digital
- Persistencia de metadatos y recuperación de datos

## Detalles Técnicos de Implementación

### Algoritmo de Asignación de Espacio
```java
// Calcula bloques necesarios según tamaño del archivo
int requiredBlocks = (int) Math.ceil((double) fileSize / LOGICAL_BLOCK_SIZE);

// Búsqueda first-fit de espacio consecutivo
int findConsecutiveFreeBlocks(int requiredBlocks) {
    int consecutive = 0;
    for (int i = 0; i < TOTAL_LOGICAL_BLOCKS; i++) {
        if (!logicalBlockMap[i]) {
            consecutive++;
            if (consecutive == requiredBlocks) 
                return i - requiredBlocks + 1;
        } else {
            consecutive = 0;
        }
    }
    return -1; // No hay espacio suficiente
}
```

### Gestión de Bitmaps
- `boolean[] logicalBlockMap`: Array de 20 posiciones para bloques lógicos
- `boolean[] physicalSectorMap`: Array de 160 posiciones para sectores físicos
- Relación: 1 bloque lógico = 8 sectores físicos consecutivos
- Actualización atómica al asignar/liberar espacio

### Mapeo Lógico-Físico
```java
// Conversión directa: cada bloque lógico comienza en sector físico = bloque * 8
int physicalSector = logicalBlock * SECTORS_PER_BLOCK;

// Al asignar N bloques consecutivos:
// - Bloques: [startBlock, startBlock+1, ..., startBlock+N-1]
// - Sectores: [startSector, startSector+1, ..., startSector+(N*8)-1]
```

### Proceso de Eliminación
```java
public void deleteFile(File file) {
    int logicalBlock = file.getMetadata().getLogicalAddress();
    int requiredBlocks = (int) Math.ceil((double) file.getMetadata().getSize() / LOGICAL_BLOCK_SIZE);
    
    // Liberar bitmaps
    for (int i = 0; i < requiredBlocks; i++) {
        logicalBlockMap[logicalBlock + i] = false;
        physicalSectorMap[(logicalBlock + i) * SECTORS_PER_BLOCK] = false;
    }
    
    // Marcar metadatos como libres (NO eliminar el objeto)
    file.getMetadata().setLogicalAddress(null);
    file.getMetadata().setPhysicalAddress(null);
}
```

### Patrón Singleton para Estado Global
```java
public class FileSystemManager {
    private static FileSystemManager instance;
    private HashMap<String, Folder> folderCache;
    
    private FileSystemManager() {
        folderCache = new HashMap<>();
        initializeFolders();
    }
    
    public static FileSystemManager getInstance() {
        if (instance == null) {
            instance = new FileSystemManager();
        }
        return instance;
    }
}
```

### Filtrado de Archivos Eliminados en UI
```java
// Solo mostrar archivos con direcciones válidas
for (File file : folder.getFiles()) {
    if (file.getMetadata().getLogicalAddress() != null && 
        file.getMetadata().getPhysicalAddress() != null) {
        // Crear panel visual para el archivo
        FileItemPanel panel = new FileItemPanel(file);
        filesPanel.add(panel);
    }
}
```

### Escaneo de Archivos Recuperables
```java
// Buscar archivos con metadatos completos pero sin direcciones
List<File> deletedFiles = new ArrayList<>();
for (Folder folder : allFolders) {
    for (File file : folder.getFiles()) {
        if (file.getMetadata().getLogicalAddress() == null) {
            deletedFiles.add(file);
        }
    }
}
```

## Limitaciones

- Sin fragmentación de archivos
- Sin persistencia en disco (todo en memoria)
- Sin sistema de permisos ni usuarios
- Operaciones simplificadas para propósitos educativos
