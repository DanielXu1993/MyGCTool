package myGCtool;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
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
    private DataWrapper dataWrapper;
    
    private XYChart chart;
    
    private SwingWrapper<XYChart> wrapper;
    
    private FlushTask ft;
    
    private JMenuItem newCon;
    
    private JMenuItem addCon;
    
    private JMenuItem heapDump;
    
    private JFrame chartFrame;
    
    public MyChart(String pid)
    {
        
        // Create XYChart and set the chart size
        chart = new XYChart(800, 600);
        chart.setXAxisTitle("time");// set the label of x axis
        chart.setYAxisTitle("size (MB)");// set the label of y axis
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        chart.getStyler().setYAxisMin(0.0);
        
        // add data series
        dataWrapper = new DataWrapper(pid);
        dataWrapper.setDataList();
        List[] data = dataWrapper.getDataList();
        // (series name,x axis value, y axis value)
        XYSeries capacity = chart.addSeries("capacity", data[0], data[1]);
        XYSeries usage = chart.addSeries("usage", data[0], data[2]);
        capacity.setMarker(SeriesMarkers.NONE);
        usage.setMarker(SeriesMarkers.NONE);
        chart.getStyler().setDatePattern("HH:mm:ss");
        // to display a Chart in a Swing
        wrapper = new SwingWrapper<>(chart);
        chartFrame = wrapper.displayChart("My GC Tool");
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
            ConnectionFrame connectionFrame = new ConnectionFrame("New Connection");
            connectionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else if (item == addCon)
        {
            System.out.println("add connection");
        }
        else if (item == heapDump)
        {
            System.out.println("heap dump");
        }
    }
    
    private class FlushTask extends SwingWorker<Void, Void>
    {
        private String chartName;
        
        public FlushTask(String chartName)
        {
            this.chartName = chartName;
        }
        
        @Override
        protected Void doInBackground()
            throws Exception
        {
            int capacityIndex = 0;
            int usageIndex = 0;
            switch (chartName)
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
                dataWrapper.setDataList();
                // get next data
                List[] newData = dataWrapper.getDataList();
                // update data series
                chart.updateXYSeries("capacity", // series name of the data which will be updated
                    newData[0], // new data in x axis
                    newData[capacityIndex], // new data in y axis
                    null); // error bar data
                
                chart.updateXYSeries("usage", // series name of the data which will be updated
                    newData[0], // new data in x axis
                    newData[usageIndex], // new data in y axis
                    null);
                
                // flush the frame
                wrapper.repaintChart();
            }
            return null;
        }
        
    }
    
}
