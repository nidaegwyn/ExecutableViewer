package executableviewer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class EntryTable extends Table {
    
    ArrayList<EntryBundle> bundles;

    public EntryTable(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    protected void LoadTable(ByteBuffer buffer) {
        bundles = new ArrayList<>();
        while (buffer.remaining() > 0) {
            EntryBundle current = new EntryBundle();
            current.numEntries = Byte.toUnsignedInt(buffer.get());
            if (current.numEntries == 0) {
                break;
            }
            current.segIndicator = Byte.toUnsignedInt(buffer.get());
            switch (current.segIndicator) {
                case 0x00:
                    bundles.add(current);
                    break;
                case 0xff:
                    current.entries = new Entries[current.numEntries];
                    for (int i = 0; i < current.numEntries; i++) {
                        current.entries[i] = new Entries();
                        current.entries[i].flags = buffer.get();
                        current.entries[i].reserved = buffer.getShort();
                        current.entries[i].segment = buffer.get();
                        current.entries[i].offset = buffer.getShort();
                    }
                    bundles.add(current);
                    break;
                default:
                    current.entries = new Entries[current.numEntries];
                    for (int i = 0; i < current.numEntries; i++) {
                        current.entries[i] = new Entries();
                        current.entries[i].flags = buffer.get();
                        current.entries[i].offset = buffer.getShort();
                    }
                    bundles.add(current);
                    break;
            }
        }
    }

    @Override
    public String toString() {
        String output = "";

        output += "Bundle #\tEntries\tIndicator\tSegment\tOffset\tFlags\n";

        for (int i = 0; i < bundles.size(); i++) {
            EntryBundle e = bundles.get(i);

            output += (i + 1) + "\t" + e.numEntries + "\t" + (e.segIndicator) + "\n";

            output += e;

        }

        return output;

    }

    public class EntryBundle {

        public int numEntries;
        public int segIndicator;
        public Entries[] entries;

        @Override
        public String toString() {
            String output = "";

            if (segIndicator == 0x00) {
                output += "\t\t(empty)\n";
            } else if (segIndicator == 0xff) {

                for (Entries e : entries) {
                    output += "\t\t\t" + e.segment + "\t" + hex(e.offset) + "h\t"
                            + ((e.flags & 0x01) == 0x01 ? "EXPORTED " : "")
                            + ((e.flags & 0x02) == 0x02 ? "GLOBAL " : "") + "\n";
                }

            } else {

                for (Entries e : entries) {
                    output += "\t\t\t\t" + hex(e.offset) + "h\t"
                            + ((e.flags & 0x01) == 0x01 ? "EXPORTED " : "")
                            + ((e.flags & 0x02) == 0x02 ? "GLOBAL " : "") + "\n";
                }
            }

            return output;
        }
    }

    public class Entries {

        public byte flags;
        public short reserved;
        public byte segment;
        public short offset;
    }
}
