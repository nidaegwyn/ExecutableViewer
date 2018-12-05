package executableviewer;

import java.nio.ByteBuffer;

public class SegmentTable extends Table {
    public SegmentEntry[] segtable;

    public SegmentTable(ByteBuffer buffer) {
        super(buffer);
    }
    
    @Override
    protected void LoadTable(ByteBuffer buffer) {
        int nEntries = buffer.remaining() / (Short.BYTES * 4);
        segtable = new SegmentEntry[nEntries];
        for (int i = 0; i < nEntries; i++) {
            segtable[i] = new SegmentEntry();
            segtable[i].offset = buffer.getShort();
            segtable[i].length = buffer.getShort();
            segtable[i].flags = buffer.getShort();
            segtable[i].minsize = buffer.getShort();
        }
    }

    @Override
    public String toString() {
        String output = "";

        output += "Entry\tOffset\tLength\tMinimum Size\tFlags\n";

        for (int i = 0; i < segtable.length; i++) {
            output += (i + 1) + "\t" + segtable[i] + "\n";
        }

        return output;
    }

    public class SegmentEntry {

        public short offset;
        public short length;
        public short flags;
        public short minsize;

        @Override
        public String toString() {
            String output = hex(offset) + "h\t" + Short.toUnsignedInt(length) + "\t" + Short.toUnsignedInt(minsize) + "\t\t" +
                            ((flags & 0x07) == 0x00? "CODE " : "") +
                            ((flags & 0x07) == 0x01? "DATA " : "") +
                            ((flags & 0x10) == 0x10? "MOVABLE " : "") +
                            ((flags & 0x20) == 0x20? "PRELOAD " : "") +
                            ((flags & 0x0100) == 0x0100? "RELOCINFO " : "") +
                            ((flags & 0xF000) == 0xF000? "DISCARD " : "");
            return output;
        }
    }
}
