package executableviewer;

import java.nio.ByteBuffer;

public class TypeInfo {
    public int typeID;
    public ResourceType resType;
    public String typeName;
    public int resourceCount;
    public int reserved;
    public NameInfo[] nameInfo;
    
    public static TypeInfo readBytes(ByteBuffer buffer) {
        
        TypeInfo typeInfo = new TypeInfo();
        
        typeInfo.typeID = Short.toUnsignedInt(buffer.getShort());
        
        if ((typeInfo.typeID & 0x8000) != 0) {
            typeInfo.resType = ResourceType.get(typeInfo.typeID ^ 0x8000);
        } else {
            int currentPos = buffer.position();
            
            int length = buffer.get(typeInfo.typeID);
            byte[] nameData = new byte[length];
            buffer.get(nameData);
            typeInfo.typeName = new String(nameData);
            
            buffer.position(currentPos);
        }
        
        typeInfo.resourceCount = Short.toUnsignedInt(buffer.getShort());
        typeInfo.reserved = buffer.getInt();
        typeInfo.nameInfo = new NameInfo[typeInfo.resourceCount];
        
        for (int i = 0; i < typeInfo.resourceCount; i++) {
            typeInfo.nameInfo[i] = NameInfo.readBytes(buffer);
        }
        
        return typeInfo;
    }
}
