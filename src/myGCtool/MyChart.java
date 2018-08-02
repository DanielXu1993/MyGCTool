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
import javax.swing.Icon;
import javax.swing.ImageIcon;
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

/**
 * This class is the main interface of the tool,
 * providing an entry for various functions and a chart showing the monitoring.
 */
public class MyChart implements ActionListener
{
    
    // data wrapper list, an item corresponds to 1 process
    private List<DataWrapper> dataWrappers;
    
    // allDataList store data list from data wrapper
    // an item represents 1 process GC data list
    private List<List[]> allDataList;
    
    // GCInfoList store GC information
    // an item represents 1 process GC information
    private List<double[]> GCInfoList;
    
    // dataLabels store all changeable labels shown in the south of frame
    // an item represents 1 process labels
    private List<JLabel[]> dataLabels;
    
    private List<String> currentPids;// current monitored process id
    
    private List<String> currentNames;// current monitored process names
    
    // the index of dataWrappers,allDataList,GCInfoList,dataLabels,currentPids,currentNames is one-to-one
    // correspondence.The data under the same index comes from the same monitored process.
    
    // allSeries store all data series shown in chart
    // a process has two series: capacity and usage
    private List<XYSeries> allSeries;
    
    private XYChart chart;// the chart
    
    private SwingWrapper<XYChart> wrapper;// used to display chart in Swing
    
    private FlushTask ft;// the flush chart task
    
    private JMenuItem newCon;// the new connection menu item
    
    private JMenuItem addCon;// the add connection menu item
    
    private JMenuItem heapDump;// the heap dump menu item
    
    private JFrame chartFrame;// the chart frame
    
    private JPanel southPanel;// the panel used to hold GC information panels
    
    /**
     * Constructor
     * 
     * @param pid first monitored process id
     * @param name first monitored process name
     */
    public MyChart(String pid, String name)
    {
        // initialize defined list
        initializeCollections();
        // set the chart frame
        setChartAttributes();
        // initialize panels
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel eastPanel = new JPanel();
        // add combo box
        addComboBox(eastPanel);
        // add button
        addGCButton(eastPanel);
        // add icon bar
        northPanel.add(addIcons(), BorderLayout.WEST);
        northPanel.add(eastPanel, BorderLayout.EAST);
        chartFrame.add(northPanel, BorderLayout.NORTH);
        // add menu
        addMenu();
        // add southern panel
        southPanel = new JPanel();
        JScrollPane jsp = new JScrollPane(southPanel);
        chartFrame.add(jsp, BorderLayout.SOUTH);
        // check whether the monitored process is running
        // to make sure the terminated process does not affect other functions
        if (Tools.isProcessRunning(pid))
        {
            this.currentPids.add(pid);// add pid to pid list
            this.currentNames.add(name);// add name to name list
            DataWrapper dataWrapper = new DataWrapper(pid);// generate GC data
            dataWrappers.add(dataWrapper);// add dataWrapper to list
            dataWrapper.addDataToList();// wrap data
            allDataList.add(dataWrapper.getDataList());// get all data and add to allDataList
            GCInfoList.add(dataWrapper.getGCInfo());// get GC information and add add to GCInfoList
        }
        // initialize a flush work,default shows heap memory
        ft = new FlushTask("Heap Memory");
        ft.execute();// execute the task
    }
    
    /**
     * the method used to initialize all the lists
     */
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
    
    /**
     * the method used to set the chart
     */
    private void setChartAttributes()
    {
        chart = new XYChart(800, 600);// Create XYChart and set the chart size
        chart.setXAxisTitle("time");// Set the label of x axis
        chart.setYAxisTitle("size (MB)");// Set the label of y axis
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);// area chart
        chart.getStyler().setYAxisMin(0.0);// The y axis start from 0.0
        chart.getStyler().setDatePattern("HH:mm:ss");// Set the format of the time displayed in x axis
        chart.getStyler().setToolTipsEnabled(true); // show tool tips
        
        // to display a Chart in a Swing
        wrapper = new SwingWrapper<>(chart);// Initialize SwingWrapper and add the chart
        // Get the chart frame which contains the chart and set the frame title
        chartFrame = wrapper.displayChart("My GC Tool");
        chartFrame.setLocation(350, 100);// Set chart frame location
        
        // add window listener to the chart frame
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
                // delete data files when close chart frame
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
    
    /**
     * Build an icon bar
     * 
     * @return icon bar panel
     */
    private JPanel addIcons()
    {
        JPanel iconPanel = new JPanel();// icon bar panel
        // Initialize icons with pictures in the icon folder
        // icon from https://www.flaticon.com/free-icon/link_126481#term=connection&page=1&position=7
        Icon newConIcon = new ImageIcon(
            this.getClass().getClassLoader().getResource("icons/connection.png"));
        // icon from https://www.flaticon.com/free-icon/chat_709641#term=add%20chat&page=1&position=10
        Icon addConIcon = new ImageIcon(
            this.getClass().getClassLoader().getResource("icons/add connection.png"));
        // icon from https://www.flaticon.com/free-icon/data-floppy-disk_31710#term=save%20data&page=1&position=12
        Icon heapDumpIcon = new ImageIcon(
            this.getClass().getClassLoader().getResource("icons/heap dump.png"));
        // icon from https://www.flaticon.com/free-icon/save_149654#term=save&page=1&position=29
        Icon saveImageIcon = new ImageIcon(
            this.getClass().getClassLoader().getResource("icons/save as image.png"));
        // set new connection icon
        JButton newConButton = new JButton(newConIcon);// create button with icon
        newConButton.setToolTipText("new connection");// add tip text
        newConButton.addActionListener(e -> newCon.doClick());// add listener,same as newCon button
        // set add connection icon
        JButton addConButton = new JButton(addConIcon);// create button with icon
        addConButton.setToolTipText("add connection");// add tip text
        addConButton.addActionListener(e -> addCon.doClick());// add listener,same as addCon button
        // set heap dump icon
        JButton heapDumpButton = new JButton(heapDumpIcon);// create button with icon
        heapDumpButton.setToolTipText("heap dump");// add tip text
        heapDumpButton.addActionListener(e -> heapDump.doClick());// add listener,same as heapDump button
        // set save as image icon
        JButton saveImageButton = new JButton(saveImageIcon);// create button with icon
        saveImageButton.setToolTipText("save as image");// add tip text
        saveImageButton.addActionListener(e -> {// add listener
            JFileChooser chooser = new JFileChooser();// used to choose file path
            chooser.setAcceptAllFileFilterUsed(false);// not show all file type
            // add file types. JPG,PNG,BMP,GIF files are allowed
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG", "jpg"));
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG", "png"));
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP", "bmp"));
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF", "gif"));
            // save dialog and the parent component is char frame.Get the state of the popdown
            int option = chooser.showSaveDialog(chartFrame);
            if (option == JFileChooser.CANCEL_OPTION)
                return;// cancel,jump out the listener
            else if (option == JFileChooser.APPROVE_OPTION)// save
            {
                String type = chooser.getFileFilter().getDescription();// get file type
                String path = chooser.getSelectedFile().getAbsolutePath();// get file absolute path
                // delete the entered suffix name.
                if (path.endsWith("." + type) || path.endsWith("." + type.toLowerCase()))
                    path = path.substring(0, path.length() - 4);
                // locate the image file
                File file = new File(path + "." + type.toLowerCase());
                if (file.exists()) // A file has existed
                {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        file.getName() + " already exists. Do you want to replace it ?",
                        "Confirm Save As", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.NO_OPTION)
                        return;// not replace existed file,jump out the listener
                        
                }
                try
                {
                    // save new image file
                    BitmapEncoder.saveBitmap(chart, path, BitmapFormat.valueOf(type));
                }
                catch (IOException e1)// an illegal path
                {
                    JOptionPane.showMessageDialog(chooser, "Illegal Path", "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
            }
            
        });
        // add icon to the bar panel
        iconPanel.add(newConButton);
        iconPanel.add(addConButton);
        iconPanel.add(heapDumpButton);
        iconPanel.add(saveImageButton);
        return iconPanel;
    }
    
    /**
     * Add a combo box to eastPanel
     * 
     * @param eastPanel the panel that holds the combo box
     */
    private void addComboBox(JPanel eastPanel)
    {
        eastPanel.add(new Label("Chart:"));// add a label
        JComboBox<String> comboBox = new JComboBox<>();// Initialize combo box
        // add combo box item
        comboBox.addItem("Heap Memory");
        comboBox.addItem("Eden Space");
        comboBox.addItem("S0 Space");
        comboBox.addItem("S1 Space");
        comboBox.addItem("Old Gen");
        comboBox.addItem("Metaspce");
        comboBox.addItemListener(e -> {// add listener
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                ft.cancel(true);// termiate other flush task
                // get selected item and execute a new task
                ft = new FlushTask((String)e.getItem());
                ft.execute();
            }
        });
        
        // add the combo box to panel
        eastPanel.add(comboBox);
    }
    
    /**
     * Add a button that used to perform GC to eastPanel
     * 
     * @param eastPanel the panel that holds the button
     */
    private void addGCButton(JPanel eastPanel)
    {
        JButton GCButton = new JButton("Perform GC");// Initialize the button
        GCButton.addActionListener(e -> Tools.performGC(currentPids));// Add listener.Perform GC
        eastPanel.add(GCButton);// add the button to the panel
    }
    
    /**
     * Add a menu to the chart frame
     */
    private void addMenu()
    {
        JMenuBar menu = new JMenuBar();// Initialize the menu bar
        chartFrame.setJMenuBar(menu);// add the menu bar to the chart frame
        
        // add menu items
        JMenu conMenu = new JMenu("Connection");
        newCon = new JMenuItem("New Connection");
        addCon = new JMenuItem("Add Connection");
        heapDump = new JMenuItem("Heap Dump");
        // add menu item listener
        newCon.addActionListener(this);
        addCon.addActionListener(this);
        heapDump.addActionListener(this);
        conMenu.add(newCon);
        conMenu.add(addCon);
        conMenu.add(heapDump);
        menu.add(conMenu);
    }
    
    /**
     * 
     * This method is used to handle events that a menu item has been selected.
     *
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object item = e.getSource(); // get selected menu item
        if (item == newCon)// new connection selected
        {
            ConnectionFrame connFrame = new ConnectionFrame("New Connection", chartFrame,
                currentPids, dataWrappers, "new");// call Connection frame
            // change Connection frame close operation to dispose
            connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else if (item == addCon)// add connection selected
        {
            ConnectionFrame connFrame = new ConnectionFrame("Add Connection", chartFrame,
                currentPids, currentNames, dataWrappers, "add");// call Connection frame
            // change Connection frame close operation to dispose
            connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else if (item == heapDump)// heap dump selected
        {
            // a swing thread to call dump frame
            new SwingWorker<Void, Void>()
            {
                protected Void doInBackground()
                    throws Exception
                {
                    new DumpFrame(currentPids, currentNames);// call dump frame
                    return null;
                }
            }.execute();
            
        }
    }
    
    /**
     * A swing thread to flush the chart in background
     */
    private class FlushTask extends SwingWorker<Void, Void>
    {
        private String chartType;// chart type selected from combo box
        
        public FlushTask(String chartType)
        {
            this.chartType = chartType;// set chart type
        }
        
        @Override
        protected Void doInBackground()
            throws Exception
        {
            int capacityIndex = 0;
            int usageIndex = 0;
            /**
             * get the data index in the allDataList according to the chart type
             */
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
                // get new data from data files
                updataDataList();
                // add entries in the data collections for new monitored processes
                addDataList();
                // add series for new monitored processes
                addSeries(capacityIndex, usageIndex);
                // add new data to existed series
                updateSeries(capacityIndex, usageIndex);
                // add GC information panel
                addGCPanel();
                // update GC information panel
                updataGCPanel();
                // flush the frame
                wrapper.repaintChart();
            }
            return null;
        }
        
        /**
         * Get and wrap new data from data files for all
         * monitored processes
         */
        private void updataDataList()
        {
            for (int i = 0; i < dataWrappers.size(); i++)
            {
                dataWrappers.get(i).addDataToList();
            }
        }
        
        /**
         * The method is executed when a new process is monitored.
         * The method can append data lists of new monitored processes to
         * the data collections
         * 
         */
        private void addDataList()
        {
            // A new process is monitored but
            // there is no corresponding entry in data collections.
            for (int i = allDataList.size(); i < dataWrappers.size(); i++)
            {
                // get the data list of new monitored process and append to
                // the data collections
                allDataList.add(dataWrappers.get(i).getDataList());
                GCInfoList.add(dataWrappers.get(i).getGCInfo());
            }
        }
        
        /**
         * The method is executed when a new process is monitored.
         * The method can add series for newly added process.
         * 
         * @param capacityIndex the index of capacity data shown in the chart
         * @param usageIndex the index of usage data shown in the chart
         */
        private void addSeries(int capacityIndex, int usageIndex)
        {
            // current series count
            int seriesCount = chart.getSeriesMap().size();
            if (seriesCount < 2 * allDataList.size())// process does not have series
            {
                // add series to allSeries list
                // the size of seriesCount should be twice the size of allDataList
                // the item from seriesCount / 2 to end in allDataList does not have series
                for (int i = seriesCount / 2; i < allDataList.size(); i++)
                {
                    // set data list based on the chart type.The x axis is the time
                    // add two series,capacity and usage and plus current index of allDataList
                    XYSeries capacity = chart.addSeries("capacity" + i,
                        allDataList.get(i)[0], allDataList.get(i)[capacityIndex]);
                    XYSeries usage = chart.addSeries("usage" + i, allDataList.get(i)[0],
                        allDataList.get(i)[usageIndex]);
                    // add series to allSeries list
                    allSeries.add(capacity);
                    allSeries.add(usage);
                    // do not show points in the chart
                    capacity.setMarker(SeriesMarkers.NONE);
                    usage.setMarker(SeriesMarkers.NONE);
                }
            }
        }
        
        /**
         * 
         * Add new data to existed series
         * 
         * @param capacityIndex the index of capacity data shown in the chart
         * @param usageIndex the index of usage data shown in the chart
         */
        private void updateSeries(int capacityIndex, int usageIndex)
        {
            // get data from allDataList and update different processes series
            for (int i = 0; i < allDataList.size(); i++)
            {
                // updateXYSeries(seriesName,new data list in x axis,new data list in y axis,error data)
                chart.updateXYSeries("capacity" + i, allDataList.get(i)[0],
                    allDataList.get(i)[capacityIndex], null);// update capacity data
                chart.updateXYSeries("usage" + i, allDataList.get(i)[0],
                    allDataList.get(i)[usageIndex], null);// update usage data
            }
        }
        
        /**
         * The method is executed when there is a monitored process but
         * without GC infomation panel in the frame.
         * The method can add a GC infomation panel for newly added process.
         */
        private void addGCPanel()
        {
            // get current GC info panel count in the southern panel
            int panelCount = southPanel.getComponentCount();
            // the size of GCInfoList is the same as the count of current monitored processes
            int infoCount = GCInfoList.size();
            if (panelCount < infoCount)// there are processes that do not have GC panel
            {
                // add GC panel for newly processes
                for (int i = panelCount; i < infoCount; i++)
                {
                    // create a GC panel and add to the southern panel
                    southPanel.add(newPanel(i));
                }
            }
        }
        
        /**
         * Update data in existed GC information panel
         * 
         */
        private void updataGCPanel()
        {
            // get labels panel by panel
            for (int i = 0; i < dataLabels.size(); i++)
            {
                // get all labels in one label
                for (int j = 0; j < dataLabels.get(i).length; j++)
                {
                    // get new data and set to the corresponding label
                    dataLabels.get(i)[j].setText(GCInfoList.get(i)[j] + "");
                }
            }
        }
        
        /**
         * Create a new GC information panel
         * 
         * @param index the index of GCInfoList,used to get data from GCInfoList
         * @return the GC information panel
         */
        private JPanel newPanel(int index)
        {
            // Create a panel which has 8 rows and 2 columns
            JPanel panel = new JPanel(new GridLayout(8, 2));
            // set panel border
            panel.setBorder(BorderFactory.createLineBorder(Color.black));
            // add series label
            JLabel series = new JLabel("Series : ");
            // add series name value :"series<index>"
            JLabel seriesValue = new JLabel("Series" + index);
            // add pid label
            JLabel pid = new JLabel("Process ID : ");
            // add pid value
            JLabel pidValue = new JLabel(currentPids.get(index));
            // add process name label
            JLabel pName = new JLabel("Process Name : ");
            // add process name value
            JLabel pNameValue = new JLabel(currentNames.get(index));
            // add minor GC count label
            JLabel MGCC = new JLabel("Minor GC Count : ");
            // add minor GC count value
            JLabel MGCCValue = new JLabel(GCInfoList.get(index)[0] + "");
            // add minor GC time label
            JLabel MGCT = new JLabel("Minor GC Time (ms) : ");
            // add minor GC time value
            JLabel MGCTValue = new JLabel(GCInfoList.get(index)[1] + "");
            // add full GC count label
            JLabel FGCC = new JLabel("Full GC Count : ");
            // add full GC count value
            JLabel FGCCValue = new JLabel(GCInfoList.get(index)[2] + "");
            // add full GC time label
            JLabel FGCT = new JLabel("Full GC Time (ms) : ");
            // add full GC time value
            JLabel FGCTValue = new JLabel(GCInfoList.get(index)[3] + "");
            // add total GC time label
            JLabel TGCT = new JLabel("Total GC Time (ms) : ");
            // add total GC time value
            JLabel TGCTValue = new JLabel(GCInfoList.get(index)[4] + "");
            // add all labels to the panel
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
            // create value label array
            JLabel[] labels =
                new JLabel[] {MGCCValue, MGCTValue, FGCCValue, FGCTValue, TGCTValue};
            // add the array to the dataLabels list
            dataLabels.add(labels);
            return panel;
        }
        
    }
    
}
