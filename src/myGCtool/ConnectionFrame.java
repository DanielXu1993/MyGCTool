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

public class ConnectionFrame extends JFrame implements ActionListener
{
    
    private JTable table;
    
    private JFrame chartFrame;
    
    private List<String> currentPids;
    
    private List<String> currentNames;
    
    private List<DataWrapper> dataWrappers;
    
    private String type;
    
    public ConnectionFrame(String title, JFrame chartFrame, List<String> currentPid,
        List<String> currentNames, List<DataWrapper> dataWrappers, String type)
    {
        this.dataWrappers = dataWrappers;
        this.type = type;
        this.currentPids = currentPid;
        this.currentNames = currentNames;
        this.chartFrame = chartFrame;
        this.setTitle(title);
        this.setSize(600, 400);
        this.setLocation(450, 250);
        
        JPanel tablePan = new JPanel(new BorderLayout());
        tablePan.add(new JLabel("Processes: "), BorderLayout.NORTH);
        String[] headings = {"pid", "Name"};
        DefaultTableModel model = new DefaultTableModel(Tools.getProcesses(), headings);
        table = new MyTable(model);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        JScrollPane jsp = new JScrollPane(table);
        tablePan.add(jsp, BorderLayout.CENTER);
        
        JPanel southPan = new JPanel(new BorderLayout());
        JPanel buttonPan = new JPanel();
        JButton flush = new JButton("Flush List");
        flush.addActionListener(e -> {
            model.getDataVector().clear();
            for (String[] row : Tools.getProcesses())
            {
                model.addRow(row);
            }
            
        });
        JButton connect = new JButton("Connect");
        connect.addActionListener(this);
        buttonPan.add(flush);
        buttonPan.add(connect);
        southPan.add(buttonPan);
        
        this.add(tablePan, BorderLayout.CENTER);
        this.add(southPan, BorderLayout.SOUTH);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (table.getSelectedRowCount() != 1)
        {
            JOptionPane.showMessageDialog(this, "please select one process", "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        int row = table.getSelectedRow();
        String pid = (String)table.getValueAt(row, 0);
        String name = (String)table.getValueAt(row, 1);
        if (type.equals("main"))
        {
            new ChartTask(pid, name).execute();
        }
        else if (type.equals("new"))
        {
            Tools.closeThread(currentPids);
            Tools.deleteCSVFile(currentPids);
            chartFrame.dispose();
            new ChartTask(pid, name).execute();
        }
        else if (type.equals("add"))
        {
            if (Tools.isThreadRunning(pid))
            {
                JOptionPane.showMessageDialog(this, "the process has been monitored",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentPids.add(pid);
            currentNames.add(name);
            dataWrappers.add(new DataWrapper(pid));
        }
        
        this.dispose();
        
    }
    
    private class ChartTask extends SwingWorker<Void, Void>
    {
        private String pid;
        
        private String name;
        
        public ChartTask(String pid, String name)
        {
            this.pid = pid;
            this.name = name;
        }
        
        @Override
        protected Void doInBackground()
            throws Exception
        {
            new MyChart(pid, name);
            return null;
        }
    }
}
