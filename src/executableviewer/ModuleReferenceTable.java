package executableviewer;

import java.nio.ByteBuffer;

public class ModuleReferenceTable extends Table {

    public short[] modreftable;

    public ModuleReferenceTable(ByteBuffer buffer) {
        super(buffer);
    }
    
    @Override
    protected void LoadTable(ByteBuffer buffer) {
        int nElements = buffer.remaining() / Short.BYTES;
        modreftable = new short[nElements];
        
        for (int i = 0; i < nElements; i++) {
            modreftable[i] = buffer.getShort();
        }
    }

    @Override
    public String toString() {
        String output = "";

        output += "Module Reference Table\n"+
                  "----------------------\n";

        for (int i = 0; i < modreftable.length; i++) {
            output += "Entry " + i + ": " + hex(modreftable[i]) + "h\n";
        }

        return output;
    }
}
