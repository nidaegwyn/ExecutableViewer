package executableviewer;

import java.nio.ByteBuffer;

public class NewExecutableHeaderTable extends Table {
    
    public String sig;
    public byte[] signature; // [2]
    public byte linkver;
    public byte linkrev;
    public short etableoffset;
    public short etablesize;
    public int crc_32;
    public short flags;
    public short autosegnum;
    public short heapsize;
    public short stacksize;
    public short ip_ptr;
    public short cs_ptr;
    public short ss_ptr;
    public short sp_ptr;
    public short nsegments;
    public short nmodules;
    public short nrnamesize;
    public short segoffset;
    public short resoffset;
    public short rnameoffset;
    public short modoffset;
    public short inameoffset;
    public int nrnameoffset;
    public short nmovable;
    public short secalign;
    public short nresources;
    public byte exetype;

    public NewExecutableHeaderTable(ByteBuffer buffer) {
        super(buffer);
    }
    
    @Override
    protected void LoadTable(ByteBuffer buffer) {
        signature = new byte[2];
        
        buffer.get(signature);
        sig = new String(signature);
        
        linkver = buffer.get();
        linkrev = buffer.get();
        etableoffset = buffer.getShort();
        etablesize = buffer.getShort();
        crc_32 = buffer.getInt();
        flags = buffer.getShort();
        autosegnum = buffer.getShort();
        heapsize = buffer.getShort();
        stacksize = buffer.getShort();
        ip_ptr = buffer.getShort();
        cs_ptr = buffer.getShort();
        sp_ptr = buffer.getShort();
        ss_ptr = buffer.getShort();
        nsegments = buffer.getShort();
        nmodules = buffer.getShort();
        nrnamesize = buffer.getShort();
        segoffset = buffer.getShort();
        resoffset = buffer.getShort();
        rnameoffset = buffer.getShort();
        modoffset = buffer.getShort();
        inameoffset = buffer.getShort();
        nrnameoffset = buffer.getInt();
        nmovable = buffer.getShort();
        secalign = buffer.getShort();
        nresources = buffer.getShort();
        exetype = buffer.get();
    }

    @Override
    public String toString() {
        String output = "";

        output += "Signature: \'" + sig + "\'\n";
        output += "Linker: v" + Byte.toUnsignedInt(linkver) + "." + Byte.toUnsignedInt(linkrev) + "\n";
        output += "Entry Table Offset: " + hex(etableoffset) + "h\n";
        output += "Entry Table Size: " + etablesize + "\n";
        output += "CRC 32: " + crc_32 + "\n";
        output += "Flags (" + flags + "): " +
                  ((flags) == 0x00? "NOAUTODATA ":"") +
                  ((flags & 0x01) == 0x01? "SINGLEDATA ":"") +
                  ((flags & 0x02) == 0x02? "MULTIPLEDATA ":"") +
                  ((flags & 0x2000) == 0x2000? "ERROR ":"") +
                  ((flags & 0x8000) == 0x8000? "LIBRARY ":"") + "\n";
        output += "Autodata Segment #: " + autosegnum + "\n";
        output += "Initial Heap Size: " + heapsize + "\n";
        output += "Initial Stack Size: " + stacksize + "\n";
        output += "CS:IP " + hex(cs_ptr) + ":" + hex(ip_ptr) + "h\n";
        output += "SS:SP " + hex(ss_ptr) + ":"+ hex(sp_ptr) + "h\n";
        output += "# Segments: " + nsegments + "\n";
        output += "# Modules: " + nmodules + "\n";
        output += "Non-Resident Name Table Size: " + nrnamesize + "\n";
        output += "Segment Table Offset: " + hex(segoffset) + "h\n";
        output += "Resource Table Offset: " + hex(resoffset) + "h\n";
        output += "Resident Name Table Offset: " + hex(rnameoffset) + "h\n";
        output += "Module Reference Table Offset: " + hex(modoffset) + "h\n";
        output += "Imported Name Table Offset: " + hex(inameoffset) + "h\n";
        output += "Non-Resident Name Table Offset: " + hex(nrnameoffset) + "h\n";
        output += "# Movable: " + nmovable + "\n";
        output += "Sector Alignment: " + secalign + "\n";
        output += "# Resources: " + nresources + "\n";
        output += "EXE Type (" + exetype + "): " + (exetype == 0x02? "WINDOWS":"UNKNOWN") + "\n";

        return output;
    }
}
