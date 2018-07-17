package myGCtool;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class MyChart
{
    private DataWrapper dataWrapper;
    
    private XYChart chart;
    
    private SwingWrapper<XYChart> wrapper;
    
    public MyChart(int pid)
    {
        
        // Create XYChart and set the chart size
        chart = new XYChart(800, 600);
        chart.setXAxisTitle("time");// set the label of x axis
        chart.setYAxisTitle("size (MB)");// set the label of y axis
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        
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
        JFrame chartFrame = wrapper.displayChart("My GC Tool");
        
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
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                flag = false;
                
                updateChart((String)e.getItem());
            }
        });
        updateChart("Heap Memory");
        
    }
    
    private volatile boolean flag;
    
    private void updateChart(String chartName)
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
        flag = true;
        while (flag)
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
    }
}
