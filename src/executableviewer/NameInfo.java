package executableviewer;

import java.nio.ByteBuffer;

public class NameInfo {
    public int offset;
    public int length;
    public int flags;
    public int ID;
    public int handle;
    public int usage;
    
    public static NameInfo readBytes(ByteBuffer buffer) {
        
        NameInfo nameInfo = new NameInfo();
        
        nameInfo.offset = Short.toUnsignedInt(buffer.getShort());
        nameInfo.length = Short.toUnsignedInt(buffer.getShort());
        nameInfo.flags = Short.toUnsignedInt(buffer.getShort());
        nameInfo.ID = Short.toUnsignedInt(buffer.getShort());
        nameInfo.handle = Short.toUnsignedInt(buffer.getShort());
        nameInfo.usage = Short.toUnsignedInt(buffer.getShort());
        
        return nameInfo;
    }
}
