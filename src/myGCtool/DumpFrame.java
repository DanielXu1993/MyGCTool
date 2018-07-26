package myGCtool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        JPanel labelPan = new JPanel(new BorderLayout());
        labelPan.add(new JLabel("The largest 10 objects:"), BorderLayout.WEST);
        labelPan.add(
            new JLabel("Time : "
                + DateTimeFormatter.ofPattern(" HH:mm:ss ").format(LocalTime.now())),
            BorderLayout.EAST);
        centerPan.add(labelPan, BorderLayout.NORTH);
        JPanel tablePan = new JPanel(new GridLayout(0, 1));
        
        for (int i = 0; i < pids.size(); i++)
        {
            JPanel innerPan = new JPanel(new BorderLayout());
            innerPan.setBorder(BorderFactory.createLineBorder(Color.black));
            JPanel titlePan = new JPanel(new BorderLayout());
            titlePan.add(new JLabel(pids.get(i) + " : " + names.get(i)),
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
            titlePan.add(table.getTableHeader(), BorderLayout.SOUTH);
            innerPan.add(titlePan, BorderLayout.NORTH);
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
