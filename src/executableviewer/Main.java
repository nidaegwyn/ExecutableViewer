package executableviewer;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.swing.JFileChooser;

public class Main {
    
    private ByteBuffer buffer;
    public String name;
    public ExecutableHeaderTable exehdr;
    public NewExecutableHeaderTable newexehdr;
    public SegmentTable segtable;
    public ResourceTable restable;
    public ResidentNameTable rnametable;
    public ModuleReferenceTable modreftable;
    public ImportedNamesTable inametable;
    public EntryTable entrytable;
    public NonResidentNameTable nrnametable;
    
    public Main(String name) {
        this(new File(name));
    }
    
    public Main(File file) {
        try {
            this.name = file.getCanonicalPath();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (this.name == null) {
                this.name = file.getAbsolutePath();
            }
        }
        
        this.buffer = LoadFile(file);

        exehdr = new ExecutableHeaderTable(buffer);
        newexehdr = new NewExecutableHeaderTable((ByteBuffer)buffer
                .position(exehdr.e_lfanew));
        segtable = new SegmentTable((ByteBuffer)buffer
                .position(exehdr.e_lfanew + newexehdr.segoffset)
                .limit(exehdr.e_lfanew + newexehdr.segoffset + newexehdr.nsegments * Short.BYTES * 4));
        restable = new ResourceTable((ByteBuffer)buffer
                .limit(exehdr.e_lfanew + newexehdr.rnameoffset)
                .position(exehdr.e_lfanew + newexehdr.resoffset));
        rnametable = new ResidentNameTable((ByteBuffer)buffer
                .limit(exehdr.e_lfanew + newexehdr.modoffset)
                .position(exehdr.e_lfanew + newexehdr.rnameoffset));
        modreftable = new ModuleReferenceTable((ByteBuffer)buffer
                .limit(exehdr.e_lfanew + newexehdr.modoffset + newexehdr.nmodules * Short.BYTES)
                .position(exehdr.e_lfanew + newexehdr.modoffset));
        inametable = new ImportedNamesTable((ByteBuffer)buffer
                .limit(exehdr.e_lfanew + newexehdr.etableoffset)
                .position(exehdr.e_lfanew + newexehdr.inameoffset));
        entrytable = new EntryTable((ByteBuffer)buffer
                .limit(exehdr.e_lfanew + newexehdr.etableoffset + newexehdr.etablesize)
                .position(exehdr.e_lfanew + newexehdr.etableoffset));
        nrnametable = new NonResidentNameTable((ByteBuffer)buffer
                .limit(newexehdr.nrnameoffset + newexehdr.nrnamesize)
                .position(newexehdr.nrnameoffset));
        InfoViewer iv = new InfoViewer(this);
        
//        WriteSegments();
//        WriteBitmaps();
//        WriteIcons();
//        WriteCursors();
    }

    public static void main(String[] args) {
        File file = null;
        if (args.length != 0) {
            file = new File(args[0]);
        }
        
        if (file == null || !file.exists()) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
            }
        }
        
        if (file != null && file.exists()) {
            new Main(file);
        }
    }

    private ByteBuffer LoadFile(String name) {
        File fh = new File(name);
        return LoadFile(fh);
    }
    
    private ByteBuffer LoadFile(File fh) {
        ByteBuffer buffer = null;
        byte[] file = new byte[(int)fh.length()];
        try (DataInputStream ds = new DataInputStream(new FileInputStream(fh))) {
            ds.readFully(file);
            buffer = ByteBuffer.wrap(file).asReadOnlyBuffer();
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return buffer;
    }

    private void WriteSegments() {
        File path = new File("SEG");
        if (!path.exists()) {
            path.mkdir();
        }
        
        for (int i = 0; i < newexehdr.nsegments; i++) {
            int offset = Short.toUnsignedInt(segtable.segtable[i].offset) << 4;
            buffer.limit(offset + Short.toUnsignedInt(segtable.segtable[i].length)).position(offset);
            ByteBuffer segment = ByteBuffer.allocateDirect(Short.toUnsignedInt(segtable.segtable[i].minsize)).order(ByteOrder.LITTLE_ENDIAN);
            segment.put(buffer).rewind();
            writeBytes("SEG\\SEG" + (i + 1) + ".dat", segment);
        }
    }
    
    private void writeBytes(String filename, ByteBuffer buffer) {
        File handle = new File(filename);
        try (FileOutputStream fs = new FileOutputStream(handle)) {
            fs.getChannel().write(buffer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private ResourceTable.ResourceEntry getResourceEntry(int typeID) {
        for (ResourceTable.ResourceEntry r : restable.restable) {
            if (Short.toUnsignedInt(r.typeID) == typeID) {
                return r;
            }
        }
        return null;
    }
    
    private ResourceTable.Resource getResource(ResourceTable.ResourceEntry entry, int resourceID) {
        for (ResourceTable.Resource r : entry.reslist) {
            if (Short.toUnsignedInt(r.resourceID) == resourceID) {
                return r;
            }
        }
        return null;
    }
    
    
    private void WriteIcons() {
        File path = new File("ICO");
        if (!path.exists()) {
            path.mkdir();
        }
        
        ResourceTable.ResourceEntry icons = getResourceEntry(0x8003);
        ResourceTable.ResourceEntry iconDir = getResourceEntry(0x800E);
        
        if (iconDir != null && icons != null) {
            for (ResourceTable.Resource r : iconDir.reslist) {
                // get offset of file[]
                int offset = Short.toUnsignedInt(r.fileOffset) << 4;
                // get length of data
                int length = Short.toUnsignedInt(r.length) << 4;
                
                buffer.limit(offset + length);
                buffer.position(offset + 4);
                int entryCount = buffer.getShort();
                
                int bufferSize = 6 + 16 * entryCount;
                for (int i = 0; i < entryCount; i++) {
                    buffer.position(offset + 6 + i * 14 + 8); // 6 for ICONDIR, 14 for ICONDIRENTRY (last field is a short), 8 for offset of size field.
                    int iconSize = buffer.getInt();
                    bufferSize += iconSize;
                }
                
                ByteBuffer ico = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.LITTLE_ENDIAN);
                buffer.limit(offset + 6).position(offset);
                ico.put(buffer); // write ICONDIR
                
                int resourceOffset = 6 + 16 * entryCount;
                for(int i = 0; i < entryCount; i++) {
                    buffer.limit(offset + 6 + i * 14 + 12).position(offset + 6 + i * 14);
                    ico.position(6 + i * 16);
                    ico.put(buffer); // write ICONDIRENTRY
                    ico.putInt(resourceOffset);
                    
                    buffer.limit(offset + 6 + (i + 1) * 14).position(offset + 6 + i * 14 + 8);
                    int iconSize = buffer.getInt();
                    int id = 0x8000 | Short.toUnsignedInt(buffer.getShort());
                    ResourceTable.Resource icon = getResource(icons, id);
                    
                    int iconOffset = Short.toUnsignedInt(icon.fileOffset) << 4;
                    buffer.limit(iconOffset + iconSize).position(iconOffset);
                    
                    ico.position(resourceOffset);
                    ico.put(buffer); // write ICON
                    resourceOffset += iconSize;
                }
                
                ico.position(0);
                writeBytes("ICO\\" + Integer.toHexString(Short.toUnsignedInt(r.resourceID)) + "h.ico", ico);
                
                // headerSize = 6 + 16 * idCount;
                // idEntries[0].dwImageOffset = headerSize;
                // idEntries[n].dwImageOffset = idEntries[n-1].dwImageOffset + idEntries[n-1].dwBytesInRes;
                
                // ICONDIR {
                // short idReserved = 0;
                // short idType = 1;
                // short idCount;
                // ICONDIRENTRY idEntries[idCount];
                // }
                
                // ICONDIRENTRY {
                // byte bWidth; // { 16, 32, 64 }
                // byte bHeight; // { 16, 32, 64 }
                // byte bColorCount; // { 2, 8, 16 }
                // byte bReserved; // { 0 }
                // short wPlanes;
                // short wBitCount;
                // int dwBytesInRes;
                // int dwImageOffset;
                // }
                
            }
        }
    }
    
    private void WriteCursors() {
        File path = new File("CUR");
        if (!path.exists()) {
            path.mkdir();
        }
        
        ResourceTable.ResourceEntry cursors = getResourceEntry(0x8001);
        ResourceTable.ResourceEntry cursorDir = getResourceEntry(0x800C);
        
        if (cursorDir != null && cursors != null) {
            for (ResourceTable.Resource r : cursorDir.reslist) {
                // get offset of file[]
                int offset = Short.toUnsignedInt(r.fileOffset) << 4;
                // get length of data
                int length = Short.toUnsignedInt(r.length) << 4;
                
                buffer.limit(offset + length);
                buffer.position(offset + 4);
                int entryCount = buffer.getShort();
                
                int bufferSize = 6 + 16 * entryCount;
                for (int i = 0; i < entryCount; i++) {
                    buffer.position(offset + 6 + i * 14 + 8); // 6 for CURSORDIR, 14 for CURSORDIRENTRY (last field is a short), 8 for offset of size field.
                    int cursorSize = buffer.getInt();
                    bufferSize += cursorSize - 4; // exclude 2*words for hotspot info.
                }
                
                ByteBuffer cur = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.LITTLE_ENDIAN);
                buffer.limit(offset + 6).position(offset);
                cur.put(buffer); // write CURSORDIR
                
                int resourceOffset = 6 + 16 * entryCount;
                for(int i = 0; i < entryCount; i++) {
                    buffer.limit(offset + 6 + (i + 1) * 14).position(offset + 6 + i * 14);
                    cur.position(6 + i * 16);
                    cur.put((byte)buffer.getShort()); // bWidth
                    cur.put((byte)buffer.getShort()); // bHeight
                    cur.put((byte)0); // bColorCount (= 0)
                    cur.put((byte)0); // bReserved (= 0)
                    cur.putShort((short)0); // wXHotspot (fill in later)
                    cur.putShort((short)0); // wYHotspot (fill in later)
                    buffer.position(offset + 6 + i * 14 + 8);
                    int cursorSize = buffer.getInt();
                    cur.putInt(cursorSize - 4); // lByteInRes
                    cur.putInt(resourceOffset); // dwImageOffset
                    
                    int id = 0x8000 | Short.toUnsignedInt(buffer.getShort());
                    ResourceTable.Resource cursor = getResource(cursors, id);
                    
                    int cursorOffset = Short.toUnsignedInt(cursor.fileOffset) << 4;
                    buffer.limit(cursorOffset + cursorSize).position(cursorOffset);
                    short xHotspot = buffer.getShort();
                    short yHotspot = buffer.getShort();
                    
                    cur.position(resourceOffset);
                    cur.put(buffer); // write CURSOR
                    cur.position(6 + i * 16 + 4);
                    cur.putShort(xHotspot);
                    cur.putShort(yHotspot);
                    resourceOffset += cursorSize;
                }
                
                cur.position(0);
                writeBytes("CUR\\" + Integer.toHexString(Short.toUnsignedInt(r.resourceID)) + "h.cur", cur);
            }
        }
    }
    
    private void WriteMenus() {
        
    }
    
    private void WriteDialogs() {
        
    }
    
    private void WriteBitmaps() {
        File path = new File("BMP");
        if (!path.exists()) {
            path.mkdir();
        }
        
        ResourceTable.ResourceEntry bitmap = getResourceEntry(0x8002);
        
        if (bitmap != null) {
            for (ResourceTable.Resource r : bitmap.reslist) {
                // get offset of file[]
                int offset = Short.toUnsignedInt(r.fileOffset) << 4;
                // get length of data
                int length = Short.toUnsignedInt(r.length) << 4;
                
                // make byteArray of size 14+length
                ByteBuffer bmp = ByteBuffer.allocateDirect(14 + length).order(ByteOrder.LITTLE_ENDIAN);

                // copy file[offset...offset+length] -> byteArray[14...14+length]
                bmp.position(14);
                buffer.limit(offset + length).position(offset);
                bmp.put(buffer);
                
                bmp.position(0);
                bmp.put((byte)'B');
                bmp.put((byte)'M');
                bmp.putInt(bmp.capacity());
                
                bmp.position(28);
                int bitsperpixel = bmp.getInt();
                
                int bmpdataoffset = 14 + 40 + (1 <<(bitsperpixel)) * 4;
                bmp.position(10);
                bmp.putInt(bmpdataoffset);
                
                bmp.position(0);
                writeBytes("BMP\\" + Integer.toHexString(Short.toUnsignedInt(r.resourceID)) + "h.bmp", bmp);
            }
        }
    }
}
