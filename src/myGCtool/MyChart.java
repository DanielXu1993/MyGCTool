package myGCtool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
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
    
    private List<double[]> GCInfoList;
    
    private List<JLabel[]> dataLabels;
    
    private XYChart chart;
    
    private SwingWrapper<XYChart> wrapper;
    
    private FlushTask ft;
    
    private JMenuItem newCon;
    
    private JMenuItem addCon;
    
    private JMenuItem heapDump;
    
    private JFrame chartFrame;
    
    private List<String> currentPids;
    
    private List<String> currentNames;
    
    private JPanel southPanel;
    
    public MyChart(String pid, String name)
    {
        initializeCollections();
        
        setChartAttributes();
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel eastPanel = new JPanel();
        addComboBox(eastPanel);
        addButtons(eastPanel);
        northPanel.add(eastPanel, BorderLayout.EAST);
        chartFrame.add(northPanel, BorderLayout.NORTH);
        addMenu();
        southPanel = new JPanel();
        JScrollPane jsp = new JScrollPane(southPanel);
        chartFrame.add(jsp, BorderLayout.SOUTH);
        
        if (Tools.isProcessRunning(pid))
        {
            this.currentPids.add(pid);
            this.currentNames.add(name);
            DataWrapper dataWrapper = new DataWrapper(pid);
            dataWrappers.add(dataWrapper);
            dataWrapper.setDataList();
            allDataList.add(dataWrapper.getDataList());
            GCInfoList.add(dataWrapper.getGCInfo());
        }
        
        ft = new FlushTask("Heap Memory");
        ft.execute();
    }
    
    private void initializeCollections()
    {
        allSeries = new ArrayList<>();
        allDataList = new ArrayList<>();
        currentPids = new ArrayList<>();
        currentNames = new ArrayList<>();
        GCInfoList = new ArrayList<>();
        dataLabels = new ArrayList<>();
        dataWrappers = new ArrayList<>();
    }
    
    private void setChartAttributes()
    {
        // Create XYChart and set the chart size
        chart = new XYChart(800, 600);
        chart.setXAxisTitle("time");// set the label of x axis
        chart.setYAxisTitle("size (MB)");// set the label of y axis
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setDatePattern("HH:mm:ss");
        
        // to display a Chart in a Swing
        wrapper = new SwingWrapper<>(chart);
        chartFrame = wrapper.displayChart("My GC Tool");
        chartFrame.setLocation(350, 150);
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
        
    }
    
    private void addComboBox(JPanel eastPanel)
    {
        eastPanel.add(new Label("Chart:"));
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Heap Memory");
        comboBox.addItem("Eden Space");
        comboBox.addItem("S0 Space");
        comboBox.addItem("S1 Space");
        comboBox.addItem("Old Gen");
        comboBox.addItem("Metaspce");
        comboBox.setSelectedItem("Heap Memory");
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                ft.cancel(true);
                ft = new FlushTask((String)e.getItem());
                ft.execute();
            }
        });
        eastPanel.add(comboBox);
    }
    
    private void addButtons(JPanel eastPanel)
    {
        JButton GCButton = new JButton("Perform GC");
        GCButton.addActionListener(e -> Tools.performGC(currentPids));
        eastPanel.add(GCButton);
        JButton saveAsImage = new JButton("save as image");
        
        saveAsImage.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG", "jpg"));
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG", "png"));
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP", "bmp"));
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF", "gif"));
            int option = chooser.showSaveDialog(chartFrame);
            if (option == JFileChooser.CANCEL_OPTION)
                return;
            else if (option == JFileChooser.APPROVE_OPTION)
            {
                String type = chooser.getFileFilter().getDescription();
                String path = chooser.getSelectedFile().getAbsolutePath();
                File file = new File(path + "." + type.toLowerCase());
                if (file.exists())
                {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        file.getName() + " already exists. Do you want to replace it ?",
                        "Confirm Save As", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.NO_OPTION)
                    {
                        return;
                    }
                    
                }
                try
                {
                    BitmapEncoder.saveBitmap(chart, path, BitmapFormat.valueOf(type));
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                
            }
            
        });
        eastPanel.add(saveAsImage);
    }
    
    private void addMenu()
    {
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
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object item = e.getSource();
        if (item == newCon)
        {
            ConnectionFrame connFrame = new ConnectionFrame("New Connection", chartFrame,
                currentPids, null, dataWrappers, "new");
            connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else if (item == addCon)
        {
            ConnectionFrame connFrame = new ConnectionFrame("Add Connection", chartFrame,
                currentPids, currentNames, dataWrappers, "add");
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
                addGCPanel();
                updataGCPanel();
                // flush the frame
                wrapper.repaintChart();
            }
            return null;
        }
        
        private void resetDataList()
        {
            for (int i = 0; i < dataWrappers.size(); i++)
            {
                dataWrappers.get(i).setDataList();
                if (dataWrappers.size() > allDataList.size())
                {
                    allDataList.add(dataWrappers.get(i).getDataList());
                    GCInfoList.add(dataWrappers.get(i).getGCInfo());
                }
                else
                {
                    allDataList.set(i, dataWrappers.get(i).getDataList());
                    GCInfoList.set(i, dataWrappers.get(i).getGCInfo());
                }
                
            }
        }
        
        private void addSeries(int capacityIndex, int usageIndex)
        {
            int seriesCount = chart.getSeriesMap().size();
            if (seriesCount < 2 * allDataList.size())
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
        
        private void addGCPanel()
        {
            int panelCount = southPanel.getComponentCount();
            int infoCount = GCInfoList.size();
            if (panelCount < infoCount)
            {
                for (int i = panelCount; i < infoCount; i++)
                {
                    southPanel.add(newPanel(i));
                }
            }
        }
        
        private void updataGCPanel()
        {
            for (int i = 0; i < dataLabels.size(); i++)
            {
                for (int j = 0; j < dataLabels.get(i).length; j++)
                {
                    dataLabels.get(i)[j].setText(GCInfoList.get(i)[j] + "");
                }
            }
        }
        
        private JPanel newPanel(int index)
        {
            JPanel panel = new JPanel(new GridLayout(8, 2));
            panel.setBorder(BorderFactory.createLineBorder(Color.black));
            JLabel series = new JLabel("Series : ");
            JLabel seriesValue = new JLabel("Series" + index);
            JLabel pid = new JLabel("Process ID : ");
            JLabel pidValue = new JLabel(currentPids.get(index));
            JLabel pName = new JLabel("Process Name : ");
            JLabel pNameValue = new JLabel(currentNames.get(index));
            JLabel MGCC = new JLabel("Minor GC Count : ");
            JLabel MGCCValue = new JLabel(GCInfoList.get(index)[0] + "");
            JLabel MGCT = new JLabel("Minor GC Time (ms) : ");
            JLabel MGCTValue = new JLabel(GCInfoList.get(index)[1] + "");
            JLabel FGCC = new JLabel("Full GC Count : ");
            JLabel FGCCValue = new JLabel(GCInfoList.get(index)[2] + "");
            JLabel FGCT = new JLabel("Full GC Time (ms) : ");
            JLabel FGCTValue = new JLabel(GCInfoList.get(index)[3] + "");
            JLabel TGCT = new JLabel("Total GC Time (ms) : ");
            JLabel TGCTValue = new JLabel(GCInfoList.get(index)[4] + "");
            panel.add(series);
            panel.add(seriesValue);
            panel.add(pid);
            panel.add(pidValue);
            panel.add(pName);
            panel.add(pNameValue);
            panel.add(MGCC);
            panel.add(MGCCValue);
            panel.add(MGCT);
            panel.add(MGCTValue);
            panel.add(FGCC);
            panel.add(FGCCValue);
            panel.add(FGCT);
            panel.add(FGCTValue);
            panel.add(TGCT);
            panel.add(TGCTValue);
            JLabel[] labels =
                new JLabel[] {MGCCValue, MGCTValue, FGCCValue, FGCTValue, TGCTValue};
            dataLabels.add(labels);
            return panel;
        }
        
    }
    
}
