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

public class ConnectionFrame extends JFrame implements ActionListener
{
    private JButton connect;
    
    private JTable table;
    
    private JFrame chartFrame;
    
    private List<String> currentPids;
    
    private List<DataWrapper> dataWrappers;
    
    private String type;
    
    public ConnectionFrame(String title, JFrame chartFrame, List<String> currentPid,
        List<DataWrapper> dataWrappers, String type)
    {
        this.dataWrappers = dataWrappers;
        this.type = type;
        this.currentPids = currentPid;
        this.chartFrame = chartFrame;
        this.setTitle(title);
        this.setSize(600, 400);
        this.setLocation(450, 250);
        
        JPanel tablePan = new JPanel(new BorderLayout());
        tablePan.add(new JLabel("Processes: "), BorderLayout.NORTH);
        
        String[] headings = {"pid", "Name"};
        table = new JTable(Tools.getProcesses(), headings)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        JScrollPane jsp = new JScrollPane(table);
        tablePan.add(jsp, BorderLayout.CENTER);
        
        JPanel southPan = new JPanel(new BorderLayout());
        JPanel buttonPan = new JPanel();
        connect = new JButton("Connect");
        connect.addActionListener(this);
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
        if (e.getSource() == connect)
        {
            if (table.getSelectedRowCount() != 1)
            {
                JOptionPane.showMessageDialog(this, "please select one process", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (type.equals("main"))
            {
                int row = table.getSelectedRow();
                String pid = (String)table.getValueAt(row, 0);
                new ChartTask(pid).execute();
            }
            else if (type.equals("new"))
            {
                Tools.closeThread(currentPids);
                Tools.deleteCSVFile(currentPids);
                currentPids.clear();
                chartFrame.dispose();
                int row = table.getSelectedRow();
                String pid = (String)table.getValueAt(row, 0);
                new ChartTask(pid).execute();
            }
            else if (type.equals("add"))
            {
                int row = table.getSelectedRow();
                String pid = (String)table.getValueAt(row, 0);
                if (Tools.isThreadRunning(pid))
                {
                    JOptionPane.showMessageDialog(this, "the process has been monitored",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                currentPids.add(pid);
                dataWrappers.add(new DataWrapper(pid));
            }
            
            this.dispose();
        }
        
    }
    
    private class ChartTask extends SwingWorker<Void, Void>
    {
        private String pid;
        
        public ChartTask(String pid)
        {
            this.pid = pid;
        }
        
        @Override
        protected Void doInBackground()
            throws Exception
        {
            new MyChart(pid);
            return null;
        }
        
    }
}
