package executableviewer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ResourceTable extends Table {

    public short resalign;
    public ArrayList<ResourceEntry> restable;
    public ArrayList<String> stringtable;

    public ResourceTable(ByteBuffer buffer) {
        super(buffer);
    }
    
    @Override
    protected void LoadTable(ByteBuffer buffer) {
        restable = new ArrayList<>();
        stringtable = new ArrayList<>();
        
        if (buffer.hasRemaining()) {
        
            resalign = buffer.getShort();
            short typeID = buffer.getShort();

            while (typeID != 0) {

                ResourceEntry current = new ResourceEntry();
                current.typeID = typeID;
                current.nresources = buffer.getShort();
                current.reslist = new Resource[current.nresources];
                current.reserved = buffer.getInt();

                for (int i = 0; i < current.nresources; i++) {

                    current.reslist[i] = new Resource();
                    current.reslist[i].fileOffset = buffer.getShort();
                    current.reslist[i].length = buffer.getShort();
                    current.reslist[i].flag = buffer.getShort();
                    current.reslist[i].resourceID = buffer.getShort();
                    current.reslist[i].reserved = buffer.getInt();
                }

                restable.add(current);
                typeID = buffer.getShort();
            }

            while (buffer.remaining() > 0) {

                int slength = buffer.get();
                byte[] stringBuffer = new byte[slength];
                buffer.get(stringBuffer);
                stringtable.add(new String(stringBuffer));
            }
        }
    }

    @Override
    public String toString() {
        String output = "";

        output += "Resource Alignment: " + resalign + "\n\n";
        output += "ResType\tCount\tFileOffset\tLength\tResID\tFlags\n";

        for (ResourceEntry r : restable) {
            output += r;
        }

        output += "\n";
        output += "String Table\n";
        output += "------------\n";

        for (String s : stringtable) {
            output += s + "\n";
        }

        return output;
    }

    public class ResourceEntry {

        public short typeID;
        public String typeName;
        public short nresources;
        public int reserved;
        public Resource[] reslist;

        @Override
        public String toString() {
            String output = "";

            output += hex(typeID) + "h\t" + nresources + "\n";

            for (Resource r : reslist) {
                output += "\t\t" + r + "\n";
            }

            return output;
        }
    }

    public class Resource {

        public short fileOffset;
        public short length;
        public short flag;
        public short resourceID;
        public int reserved;

        @Override
        public String toString() {
            String output = "";

            output += hex((int)(fileOffset & 0xffff) << 4) + "h\t" + (length << 4) + "\t" + hex(resourceID) + "h\t" +
                    hex(flag) + "h (" +
                    ((flag & 0x10) == 0x10 ? "MOVABLE " : "FIXED ") +
                    ((flag & 0x20) == 0x20 ? "PURE " : "") +
                    ((flag & 0x40) == 0x40 ? "PRELOAD " : "LOADONCALL ") +
                    ((flag & 0x1000) == 0x1000 ? "DISCARDABLE " : "") +
                    ")";

            return output;
        }
    }
}
