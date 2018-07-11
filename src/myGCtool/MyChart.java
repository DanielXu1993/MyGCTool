package myGCtool;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;

public class MyChart
{
    public MyChart(String pid)
    {
        
        // Create XYChart and set the chart size
        XYChart chart = new XYChart(600, 400);
        chart.setXAxisTitle("time (s)");// set the label of x axis
        chart.setYAxisTitle("size (MB)");// set the label of y axis
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        
        // add data series
        int phase = 0;
        DataWrapper dataWrapper = new DataWrapper(pid);
        double[][] initData = dataWrapper.getHeapUsage(phase);
        // (series name,x axis value, y axis value)
        chart.addSeries("capacity", initData[0], initData[1]);
        chart.addSeries("usage", initData[0], initData[2]);
        
        // to display a Chart in a Swing
        SwingWrapper<XYChart> wrapper = new SwingWrapper<>(chart);
        JFrame displayChart = wrapper.displayChart("My GC Tool");
        displayChart.addWindowListener(new WindowListener()
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
                String path = System.getProperty("user.dir") + "\\" + pid + ".csv";
                File file = new File(path);
                file.delete();
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
        
        // Dynamic update chart
        while (true)
        {
            phase++;
            
            // get next data
            double[][] data = dataWrapper.getHeapUsage(phase);
            // a thread to flush the chart
            SwingUtilities.invokeLater(() -> {
                // update data series
                chart.updateXYSeries("capacity", // series name of the data which will be updated
                    data[0], // new data in x axis
                    data[1], // new data in y axis
                    null); // error bar data
                
                chart.updateXYSeries("usage", // series name of the data which will be updated
                    data[0], // new data in x axis
                    data[2], // new data in y axis
                    null);
                
                // flush the frame
                wrapper.repaintChart();
            });
            
        }
    }
    
    public static void main(String[] args)
    {
        new MyChart("6612");
    }
    
}
