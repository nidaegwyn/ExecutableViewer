package executableviewer;

import java.nio.ByteBuffer;

public class ExecutableHeaderTable extends Table {
    
    public String sig; // string representation of the signature
    public byte[] signature; //[2]
    public short lastsize;
    public short nblocks;
    public short nreloc;
    public short hdrsize;
    public short minalloc;
    public short maxalloc;
    public short ss_ptr;
    public short sp_ptr;
    public short checksum;
    public short ip_ptr;
    public short cs_ptr;
    public short relocpos;
    public short noverlay;
    public short[] reserved1; //[4];
    public short oem_id;
    public short oem_info;
    public short[] reserved2; //[10];
    public int  e_lfanew;

    public ExecutableHeaderTable(ByteBuffer buffer) {
        super(buffer);
    }
    
    @Override
    protected void LoadTable(ByteBuffer data) {
        signature = new byte[2];
        reserved1 = new short[4];
        reserved2 = new short[10];
        
        data.get(signature);
        sig = new String(signature);

        lastsize = data.getShort();
        nblocks = data.getShort();
        nreloc = data.getShort();
        hdrsize = data.getShort();
        minalloc = data.getShort();
        maxalloc = data.getShort();
        ss_ptr = data.getShort();
        sp_ptr = data.getShort();
        checksum = data.getShort();
        ip_ptr = data.getShort();
        cs_ptr = data.getShort();
        relocpos = data.getShort();
        noverlay = data.getShort();
        
        for (int i = 0; i < 4; i++) {
            reserved1[i] = data.getShort();
        }

        oem_id = data.getShort();
        oem_info = data.getShort();

        for (int i = 0; i < 10; i++) {
            reserved2[i] = data.getShort();
        }

        e_lfanew = data.getInt();
    }

    @Override
    public String toString() {
        String output = "";

        output += "Signature: \'" + sig + "\'\n";
        output += "Part Last Page: " + lastsize + " bytes\n";
        output += "Page Count: " + nblocks + " pages\n";
        output += "Relocations Count: " + nreloc + "\n";
        output += "Header Size: " + hdrsize + " paragraphs\n";
        output += "Minimum Memory: " + hex(minalloc) + "h paragraphs\n";
        output += "Maximum Memory: " + hex(maxalloc) + "h paragraphs\n";
        output += "SS:SP " + hex(ss_ptr) + ":" + hex(sp_ptr) + "h\n";
        output += "Checksum: " + checksum + "\n";
        output += "CS:IP " + hex(cs_ptr)+":"+hex(ip_ptr) + "h\n";
        output += "Table Offset: " + hex(relocpos) + "h bytes\n";
        output += "Overlay Number: " + noverlay + "\n";
        //reserved1 //[4]
        output += "OEM id: " + hex(oem_id) + "h\n";
        output += "OEM info: " + hex(oem_info) + "h\n";
        //reserved2 //[10]
        output += "New Exe Header offset: " + hex(e_lfanew) + "h\n";

        return output;
    }
}
