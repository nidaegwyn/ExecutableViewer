package executableviewer;

import java.nio.ByteBuffer;

public abstract class Table {
    
    public Table(ByteBuffer data) {
        LoadTable(data);
    }
    
    protected abstract void LoadTable(ByteBuffer data);
    
    protected String hex(short num) {
        String output = Integer.toHexString(num & 0xffff);
        if (output.length() < 4) {
            output = "0000".substring(0, 4-output.length()) + output;
        }
        output = output.toUpperCase();
        return output;
    }

    protected String hex(int num) {
        String output = Integer.toHexString(num);
        if (output.length() < 8) {
            output = "00000000".substring(0, 8-output.length()) + output;
        }
        output = output.toUpperCase();
        return output;
    }
}
