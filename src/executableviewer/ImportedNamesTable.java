package executableviewer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ImportedNamesTable extends Table {

    public ArrayList<String> inametable;

    public ImportedNamesTable(ByteBuffer buffer) {
        super(buffer);
    }
    
    @Override
    protected void LoadTable(ByteBuffer buffer) {
        inametable = new ArrayList<>();
        
        byte length;
        
        while (buffer.hasRemaining()) {

            length = buffer.get();
            byte[] nameBuffer = new byte[length];
            buffer.get(nameBuffer);
            inametable.add(new String(nameBuffer));
        }
    }
    
    @Override
    public String toString() {
        String output = "";
        output += "Imported Names\n";
        for (String s : inametable) {
            output += s + "\n";
        }
        return output;
    }
}
