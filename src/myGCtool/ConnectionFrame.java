package myGCtool;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    
    public ConnectionFrame(String title)
    {
        this.setTitle(title);
        this.setSize(600, 400);
        this.setLocation(450, 250);
        
        JPanel tablePan = new JPanel(new BorderLayout());
        tablePan.add(new JLabel("Processes: "), BorderLayout.NORTH);
        
        String[] headings = {"pid", "Name"};
        table = new JTable(getApp(), headings)
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
            int row = table.getSelectedRow();
            if (row == -1)
            {
                JOptionPane.showMessageDialog(this, "no process selected", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.dispose();
            String pid = (String)table.getValueAt(row, 0);
            new ChartTask(pid).execute();
        }
    }
    
    private String[][] getApp()
    {
        Map<String, String> apps = new HashMap<>();
        BufferedReader reader = null;
        try
        {
            Process exec = Runtime.getRuntime().exec("jps");
            reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                String[] strs = line.split(" ");
                apps.put(strs[0], strs[1]);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        String[][] strs = new String[apps.size()][2];
        Set<Entry<String, String>> entries = apps.entrySet();
        int index = 0;
        for (Entry<String, String> entry : entries)
        {
            strs[index] = new String[] {entry.getKey(), entry.getValue()};
            index++;
        }
        
        return strs;
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
