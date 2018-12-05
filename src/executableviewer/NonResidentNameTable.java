package executableviewer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class NonResidentNameTable extends Table {

    public ArrayList<NonResidentNameEntry> rnametable;

    public NonResidentNameTable(ByteBuffer buffer) {
        super(buffer);
    }
    
    @Override
    protected void LoadTable(ByteBuffer buffer) {
        rnametable = new ArrayList<>();
        
        byte length;

        do {
            
            length = buffer.get();
            
            if (length > 0) {
                NonResidentNameEntry current = new NonResidentNameEntry();
                current.length = length;

                byte[] nameBuffer = new byte[length];
                buffer.get(nameBuffer);
                current.name = new String(nameBuffer);
                current.ordinal = buffer.getShort();
                rnametable.add(current);
            }
        } while (length > 0);
    }
    
    @Override
    public String toString() {
        String output = "";
        output += "Name\tOrdinal\n";
        for (NonResidentNameEntry r : rnametable) {
            output += r + "\n";
        }
        return output;
    }

    public class NonResidentNameEntry {

        public byte length;
        public String name;
        public short ordinal;

        @Override
        public String toString() {
            String output = "";
            output += name + "\t\t" + ordinal;
            return output;
        }
    }
}
