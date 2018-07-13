package myGCtool;

import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;

public class MyChart
{
    public MyChart(int pid)
    {
        
        // Create XYChart and set the chart size
        XYChart chart = new XYChart(600, 400);
        chart.setXAxisTitle("time");// set the label of x axis
        chart.setYAxisTitle("size (MB)");// set the label of y axis
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        
        // add data series
        int phase = 0;
        DataWrapper dataWrapper = new DataWrapper(pid);
        List<Double>[] data = dataWrapper.getHeapUsage(phase);
        // (series name,x axis value, y axis value)
        chart.addSeries("capacity", data[0], data[1]);
        chart.addSeries("usage", data[0], data[2]);
        chart.getStyler().setDatePattern("HH:mm:ss");
        // to display a Chart in a Swing
        SwingWrapper<XYChart> wrapper = new SwingWrapper<>(chart);
        JFrame displayChart = wrapper.displayChart("My GC Tool");
        
        
        // Dynamic update chart
        while (true)
        {
            phase++;
            
            // get next data
            List<Double>[] newData = dataWrapper.getHeapUsage(phase);
            // a thread to flush the chart
            SwingUtilities.invokeLater(() -> {
                // update data series
                chart.updateXYSeries("capacity", // series name of the data which will be updated
                    newData[0], // new data in x axis
                    newData[1], // new data in y axis
                    null); // error bar data
                
                chart.updateXYSeries("usage", // series name of the data which will be updated
                    newData[0], // new data in x axis
                    newData[2], // new data in y axis
                    null);
                
                // flush the frame
                wrapper.repaintChart();
            });
            
        }
    }
    
    public static void main(String[] args)
    {
        deleteCSVFile();
        new MyChart(16884);
    }
    
    private static void deleteCSVFile()
    {
        String path = System.getProperty("user.dir");
        File dir = new File(path);
        File[] files = dir.listFiles();
        
        for (File file : files)
        {
            if (file.getName().endsWith(".csv"))
            {
                file.delete();
            }
        }
    }
    
}
