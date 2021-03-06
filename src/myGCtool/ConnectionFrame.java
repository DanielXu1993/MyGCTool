package myGCtool;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * This class is a frame class. It provides all the java processes that can be monitored.
 * The entry to the program, the "new connection", and "add connection" need to call this frame.
 */
public class ConnectionFrame extends JFrame implements ActionListener
{
    
    private JTable table;// the table to show all java processes
    
    private List<String> currentPids;// all monitored process id
    
    private List<String> currentNames;// all monitored process name
    
    private List<DataWrapper> dataWrappers; // current monitored process data
    
    // the type of the frame,can be "main","new","add".
    // Represents the function of the current frame
    private String type;
    
    private JButton flush;// the flush button
    
    private ProcessData processes;
    
    /**
     * Constructor,mainly called by the entry of the program
     * 
     * @param title the title of the frame
     * @param type the function of the frame
     */
    public ConnectionFrame(String title, String type)
    {
        this(title, null, null, null, type);
    }
    
    /**
     * Constructor,mainly called when have a new connection
     * 
     * @param title the title of the frame
     * @param currentPids all monitored process id
     * @param dataWrappers current monitored process data
     * @param type the function of the frame
     */
    public ConnectionFrame(String title, List<String> currentPids,
        List<DataWrapper> dataWrappers, String type)
    {
        this(title, currentPids, null, dataWrappers, type);
    }
    
    /**
     * Constructor,mainly called when add a connection
     * 
     * @param title the title of the frame
     * @param currentPids all monitored process id
     * @param currentNames all monitored process names
     * @param dataWrappers current monitored process data
     * @param type the function of the frame
     */
    public ConnectionFrame(String title, List<String> currentPids,
        List<String> currentNames, List<DataWrapper> dataWrappers, String type)
    {
        processes = new ProcessData();
        // set instance variables
        this.dataWrappers = dataWrappers;
        this.type = type;
        this.currentPids = currentPids;
        this.currentNames = currentNames;
        
        this.setTitle(title);// set frame title
        this.setSize(600, 400); // set frame size
        this.setLocation(450, 250);// set frame location
        
        JPanel tablePan = new JPanel(new BorderLayout());// a panel to hold table
        tablePan.add(new JLabel("Processes: "), BorderLayout.NORTH);// add "Processes:" label
        String[] headings = {"pid", "Name"};// table title
        
        // get table data and add data to DefaultTableModel
        DefaultTableModel model =
            new DefaultTableModel(processes.getProcesses(), headings);
        table = new MyTable(model);// Instantiate table
        
        // set table column width
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        // add scroll to show the table
        JScrollPane jsp = new JScrollPane(table);
        // ad the scroll to the panel
        tablePan.add(jsp, BorderLayout.CENTER);
        
        // the southern panel in the frame
        JPanel southPan = new JPanel(new BorderLayout());
        
        JPanel buttonPan = new JPanel();// the panel to hold buttons
        flush = new JButton("Refresh List");// Instantiate flush button
        flush.addActionListener(e -> {// add listener to flush button
            model.getDataVector().clear();// clear all data in the table
            for (String[] row : processes.getProcesses())// get new data
            {
                model.addRow(row);// add new data to the table line by line
            }
            table.clearSelection();// cancel the selection
        });
        JButton connect = new JButton("Connect");// Instantiate connect button
        connect.addActionListener(this);// add listener
        
        // add buttons to the button panel
        buttonPan.add(flush);
        buttonPan.add(connect);
        // add button panel to the southern panel
        southPan.add(buttonPan);
        
        // add table to the frame
        this.add(tablePan, BorderLayout.CENTER);
        // add southern panel to the frame
        this.add(southPan, BorderLayout.SOUTH);
        this.pack();// Resize this frame
        this.setVisible(true);// set this frame visible
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// set frame close
    }
    
    /**
     * This method is used to handle events that have the connect button clicked.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        // No selection or multiple rows selected
        if (table.getSelectedRowCount() != 1)
        {
            JOptionPane.showMessageDialog(this, "please select one process", "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        int row = table.getSelectedRow();// selected row
        String pid = (String)table.getValueAt(row, 0);// data in selected row and 1st column
        String name = (String)table.getValueAt(row, 1);// data in selected row and 2nd column
        
        if (!processes.isProcessRunning(pid))// selected process has been terminated
        {
            JOptionPane.showMessageDialog(this, "the process has terminated", "Error",
                JOptionPane.ERROR_MESSAGE);
            flush.doClick();// flush the table
            return;
        }
        if (type.equals("main"))// entry of the program calls the frame
            new ChartTask(pid, name).execute();// start a task to build chart frame
        else if (type.equals("new"))// new connection calls the frame
            new ChartTask(pid, name).execute();// start a task to build a new chart frame
        else if (type.equals("add"))// add connection calls the frame
        {
            if (Tools.isThreadRunning(pid))// selected process has been monitored in the chart frame
            {
                JOptionPane.showMessageDialog(this, "the process has been monitored",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            currentPids.add(pid);// add new pid
            currentNames.add(name);// add new process name
            dataWrappers.add(new DataWrapper(pid));// add new process data
            
        }
        this.dispose();// this frame disposes
    }
    
    /**
     * A swing thread to build chart frame in background
     */
    private class ChartTask extends SwingWorker<Void, Void>
    {
        private String pid;// current monitored pid
        
        private String name;// process name
        
        public ChartTask(String pid, String name)
        {
            this.pid = pid;// set pid
            this.name = name;// set process name
        }
        
        @Override
        protected Void doInBackground()
            throws Exception
        {
            new MyChartFrame(pid, name);// build new chart frame
            return null;
        }
    }
}
