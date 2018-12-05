package executableviewer;

public enum ResourceType {
    Unknown(0),
    
    Cursor(1),
    Bitmap(2),
    Icon(3),
    Menu(4),
    Dialog(5),
    String(6),
    FontDir(7),
    Font(8),
    Accelerator(9),
    Data(10),
    
    CursorGroup(12),
    IconGroup(14);
    
    public int value;
    
    private ResourceType(int value) {
        this.value = value;
    }
    
    public static ResourceType get(int value) {
        for (ResourceType rt : ResourceType.values()) {
            if (rt.value == value) return rt;
        }
        
        return ResourceType.Unknown;
    }
}
