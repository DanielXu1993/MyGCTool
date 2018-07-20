package myGCtool;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class MyChart implements ActionListener
{
    private List<DataWrapper> dataWrappers;
    
    private List<XYSeries> allSeries;
    
    private List<List[]> allDataList;
    
    private XYChart chart;
    
    private SwingWrapper<XYChart> wrapper;
    
    private FlushTask ft;
    
    private JMenuItem newCon;
    
    private JMenuItem addCon;
    
    private JMenuItem heapDump;
    
    private JFrame chartFrame;
    
    private List<String> currentPids;
    
    public MyChart(String pid)
    {
        allSeries = new ArrayList<>();
        allDataList = new ArrayList<>();
        currentPids = new ArrayList<>();
        this.currentPids.add(pid);
        // Create XYChart and set the chart size
        chart = new XYChart(800, 600);
        chart.setXAxisTitle("time");// set the label of x axis
        chart.setYAxisTitle("size (MB)");// set the label of y axis
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        chart.getStyler().setYAxisMin(0.0);
        dataWrappers = new ArrayList<>();
        // add data series
        DataWrapper dataWrapper = new DataWrapper(pid);
        dataWrappers.add(dataWrapper);
        dataWrapper.setDataList();
        allDataList.add(dataWrapper.getDataList());
        chart.getStyler().setDatePattern("HH:mm:ss");
        // to display a Chart in a Swing
        wrapper = new SwingWrapper<>(chart);
        chartFrame = wrapper.displayChart("My GC Tool");
        chartFrame.addWindowListener(new WindowListener()
        {
            
            @Override
            public void windowOpened(WindowEvent e)
            {
            }
            
            @Override
            public void windowIconified(WindowEvent e)
            {
            }
            
            @Override
            public void windowDeiconified(WindowEvent e)
            {
            }
            
            @Override
            public void windowDeactivated(WindowEvent e)
            {
            }
            
            @Override
            public void windowClosing(WindowEvent e)
            {
                Tools.closeThread(currentPids);
                Tools.deleteCSVFile(currentPids);
            }
            
            @Override
            public void windowClosed(WindowEvent e)
            {
                
            }
            
            @Override
            public void windowActivated(WindowEvent e)
            {
            }
        });
        chartFrame.setLocation(350, 150);
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel eastPanel = new JPanel();
        JPanel selector = new JPanel();
        selector.add(new Label("Chart:"));
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Heap Memory");
        comboBox.addItem("Eden Space");
        comboBox.addItem("S0 Space");
        comboBox.addItem("S1 Space");
        comboBox.addItem("Old Gen");
        comboBox.addItem("Metaspce");
        comboBox.setSelectedItem("Heap Memory");
        selector.add(comboBox);
        eastPanel.add(selector);
        northPanel.add(eastPanel, BorderLayout.EAST);
        chartFrame.add(northPanel, BorderLayout.NORTH);
        JMenuBar menu = new JMenuBar();
        chartFrame.setJMenuBar(menu);
        JMenu conMenu = new JMenu("Connection");
        newCon = new JMenuItem("New Connection");
        addCon = new JMenuItem("Add Connection");
        heapDump = new JMenuItem("Heap Dump");
        newCon.addActionListener(this);
        addCon.addActionListener(this);
        heapDump.addActionListener(this);
        conMenu.add(newCon);
        conMenu.add(addCon);
        conMenu.add(heapDump);
        menu.add(conMenu);
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                ft.cancel(true);
                ft = new FlushTask((String)e.getItem());
                ft.execute();
            }
        });
        ft = new FlushTask("Heap Memory");
        ft.execute();
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object item = e.getSource();
        if (item == newCon)
        {
            ConnectionFrame connFrame = new ConnectionFrame("New Connection", chartFrame,
                currentPids, dataWrappers, "new");
            connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else if (item == addCon)
        {
            ConnectionFrame connFrame = new ConnectionFrame("Add Connection", chartFrame,
                currentPids, dataWrappers, "add");
            connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else if (item == heapDump)
        {
            System.out.println("heap dump");
        }
    }
    
    private class FlushTask extends SwingWorker<Void, Void>
    {
        private String chartType;
        
        public FlushTask(String chartType)
        {
            this.chartType = chartType;
        }
        
        @Override
        protected Void doInBackground()
            throws Exception
        {
            int capacityIndex = 0;
            int usageIndex = 0;
            switch (chartType)
            {
                case "Eden Space":
                    capacityIndex = 7;
                    usageIndex = 8;
                    break;
                case "S0 Space":
                    capacityIndex = 3;
                    usageIndex = 4;
                    break;
                case "S1 Space":
                    capacityIndex = 5;
                    usageIndex = 6;
                    break;
                case "Old Gen":
                    capacityIndex = 9;
                    usageIndex = 10;
                    break;
                case "Metaspce":
                    capacityIndex = 11;
                    usageIndex = 12;
                    break;
                default:
                    capacityIndex = 1;
                    usageIndex = 2;
            }
            while (!isCancelled())
            {
                resetDataList();
                addSeries(capacityIndex, usageIndex);
                updateSeries(capacityIndex, usageIndex);
                // flush the frame
                wrapper.repaintChart();
            }
            return null;
        }
        
        private void updateSeries(int capacityIndex, int usageIndex)
        {
            for (int i = 0; i < allDataList.size(); i++)
            {
                chart.updateXYSeries("capacity" + i, allDataList.get(i)[0],
                    allDataList.get(i)[capacityIndex], null);
                
                chart.updateXYSeries("usage" + i, allDataList.get(i)[0],
                    allDataList.get(i)[usageIndex], null);
            }
        }
        
        private void addSeries(int capacityIndex, int usageIndex)
        {
            int seriesCount = chart.getSeriesMap().size();
            if (seriesCount == 0)
            {
                for (int i = 0; i < allDataList.size(); i++)
                {
                    XYSeries capacity = chart.addSeries("capacity" + i,
                        allDataList.get(i)[0], allDataList.get(i)[capacityIndex]);
                    XYSeries usage = chart.addSeries("usage" + i, allDataList.get(i)[0],
                        allDataList.get(i)[usageIndex]);
                    allSeries.add(capacity);
                    allSeries.add(usage);
                    capacity.setMarker(SeriesMarkers.NONE);
                    usage.setMarker(SeriesMarkers.NONE);
                }
            }
            else if (seriesCount < 2 * allDataList.size())
            {
                for (int i = seriesCount / 2; i < allDataList.size(); i++)
                {
                    
                    XYSeries capacity = chart.addSeries("capacity" + i,
                        allDataList.get(i)[0], allDataList.get(i)[capacityIndex]);
                    XYSeries usage = chart.addSeries("usage" + i, allDataList.get(i)[0],
                        allDataList.get(i)[usageIndex]);
                    allSeries.add(capacity);
                    allSeries.add(usage);
                    capacity.setMarker(SeriesMarkers.NONE);
                    usage.setMarker(SeriesMarkers.NONE);
                }
            }
        }
        
        private void resetDataList()
        {
            allSeries.clear();
            for (int i = 0; i < dataWrappers.size(); i++)
            {
                dataWrappers.get(i).setDataList();
                if (dataWrappers.size() > allDataList.size())
                {
                    allDataList.add(dataWrappers.get(i).getDataList());
                }
                else
                {
                    allDataList.set(i, dataWrappers.get(i).getDataList());
                }
                
            }
        }
        
    }
    
}
