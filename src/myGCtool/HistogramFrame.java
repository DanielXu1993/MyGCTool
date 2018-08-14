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

/**
 * This class is a frame class. It displays heap histogram data.
 */
public class HistogramFrame extends JFrame
{
    private HistogramData histogramData;// histogram data
    
    /**
     * Constructor
     * 
     * @param pids pids that used to generate the heap histogram data
     * @param names process names
     */
    public HistogramFrame(List<String> pids, List<String> names)
    {
        histogramData = new HistogramData();// initialize histogramData
        this.setTitle("Heap Histogram");// set frame title
        this.setSize(800, 400);// set frame size
        this.setLocation(410, 240);// set frame location
        
        JPanel centerPan = new JPanel(new BorderLayout());
        JPanel labelPan = new JPanel(new BorderLayout());
        // add label "The largest 10 objects:"
        labelPan.add(new JLabel("The largest 10 objects:"), BorderLayout.WEST);
        // add current time label
        labelPan.add(
            new JLabel("Time : "
                + DateTimeFormatter.ofPattern(" HH:mm:ss ").format(LocalTime.now())),
            BorderLayout.EAST);
        centerPan.add(labelPan, BorderLayout.NORTH);
        // each tablePan line contains 1 component
        JPanel tablePan = new JPanel(new GridLayout(0, 1));
        
        for (int i = 0; i < pids.size(); i++)// add table panel,a pid has 1 table panel
        {
            // a innerPan contains one process data
            JPanel innerPan = new JPanel(new BorderLayout());
            // set panel border
            innerPan.setBorder(BorderFactory.createLineBorder(Color.black));
            
            JPanel titlePan = new JPanel(new BorderLayout());// title panel
            titlePan.add(new JLabel(pids.get(i) + " : " + names.get(i)),
                BorderLayout.NORTH);// table title: "<pid>:<process name>"
            // table headings
            String[] headings =
                {" ", "instances(percentage%)", "size(byte)(percentage%)", "class name"};
            
            // get table data and add data to DefaultTableModel
            DefaultTableModel model =
                new DefaultTableModel(histogramData.rowData(pids.get(i)), headings);
            MyTable table = new MyTable(model);// Instantiate table
            // set table column width
            table.getColumnModel().getColumn(0).setPreferredWidth(130);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(290);
            // add table heading to the south of the title panel
            // due to table does not contain the heading when it does not have a scroll
            titlePan.add(table.getTableHeader(), BorderLayout.SOUTH);
            innerPan.add(titlePan, BorderLayout.NORTH);// add title to the north of inner panel
            innerPan.add(table);// add table to the center of inner panel
            tablePan.add(innerPan);// add inner panel to the table panel
        }
        centerPan.add(tablePan); // add table panel to the center panel
        JScrollPane jsp = new JScrollPane(centerPan);// add scroll to the center panel
        
        this.add(jsp);// add scroll to the frame
        this.setVisible(true);// set frame visible
        this.repaint();// flush the frame
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);// set frame close
    }
}
