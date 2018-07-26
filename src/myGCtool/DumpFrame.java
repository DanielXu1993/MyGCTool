package myGCtool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

public class DumpFrame extends JFrame
{
    public DumpFrame(List<String> pids, List<String> names)
    {
        this.setTitle("Heap Dump");
        this.setSize(740, 400);
        this.setLocation(410, 240);
        
        JPanel centerPan = new JPanel(new BorderLayout());
        centerPan.add(new JLabel("The largest 10 objects:"), BorderLayout.NORTH);
        JPanel tablePan = new JPanel(new GridLayout(0, 1));
        
        for (int i = 0; i < pids.size(); i++)
        {
            JPanel innerPan = new JPanel(new BorderLayout());
            innerPan.setBorder(BorderFactory.createLineBorder(Color.black));
            JPanel labelPan = new JPanel(new BorderLayout());
            labelPan.add(new JLabel(pids.get(i) + " : " + names.get(i)),
                BorderLayout.NORTH);
            String[] headings =
                {" ", "instances(percentage%)", "size(byte)(percentage%)", "class name"};
            DefaultTableModel model =
                new DefaultTableModel(new DumpData().rowData(pids.get(i)), headings);
            MyTable table = new MyTable(model);
            table.getColumnModel().getColumn(0).setPreferredWidth(130);
            table.getColumnModel().getColumn(1).setPreferredWidth(130);
            table.getColumnModel().getColumn(2).setPreferredWidth(130);
            table.getColumnModel().getColumn(3).setPreferredWidth(290);
            labelPan.add(table.getTableHeader(), BorderLayout.SOUTH);
            innerPan.add(labelPan, BorderLayout.NORTH);
            innerPan.add(table);
            tablePan.add(innerPan);
        }
        centerPan.add(tablePan);
        JScrollPane jsp = new JScrollPane(centerPan);
        
        this.add(jsp);
        this.setVisible(true);
        this.repaint();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
