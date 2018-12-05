package executableviewer;

import javax.swing.*;
import java.awt.*;

public class InfoViewer extends JFrame {

    Main app;

    public InfoViewer(Main app) {

        super(app.name);
        this.app = app;

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tb = new JTabbedPane();
        
        createTab(tb, "Old Executable Header", app.exehdr);
        createTab(tb, "New Executable Header", app.newexehdr);
        createTab(tb, "Segment Table", app.segtable);
        createTab(tb, "Resource Table", app.restable);
        createTab(tb, "Resident Name Table", app.rnametable);
        createTab(tb, "Module Reference Table", app.modreftable);
        createTab(tb, "Entry Table", app.entrytable);
        createTab(tb, "Import Name Table", app.inametable);
        createTab(tb, "Non Resident Name Table", app.nrnametable);

        add(tb);
        setPreferredSize(new Dimension(1024, 768));
        pack();
        setVisible(true);
    }
    
    private void createTab(JTabbedPane tb, String title, Table table) {
        JTextArea tp = new JTextArea();
        tp.setText(table.toString());
        tp.setEditable(false);
        tb.addTab(title, new JScrollPane(tp));
    }
}
