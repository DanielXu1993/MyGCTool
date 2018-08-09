package myGCtool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class wraps all GC data for a monitored process.
 * 
 * An instance of this class represents all GC data for a process.
 */
public class DataWrapper
{
    private String pid; // current instance represents the GC data for this pid.
    
    private List<String> dataLines;// data lines from Jstat
    
    // The index of the beginning of data line to be processed next time in dataLines
    private int index = 0;
    
    /**
     * The following ArrayList stores different types of data.
     * Their size is the same. The data under the index represents the dataLines index.
     */
    // store time, the first column of data
    private ArrayList<Date> timeList = new ArrayList<Date>();
    
    // store heap capacity, the total of the s0 capacity,s1 capacity,eden capacity and old generation capacity
    private ArrayList<Double> heapCapacity = new ArrayList<>();
    
    // store heap usage, the total of the s0 usage,s1 usage,eden usage and old generation usage
    private ArrayList<Double> heapUsage = new ArrayList<>();
    
    // store S0 space capacity,the second column of data 
    private ArrayList<Double> s0Capacity = new ArrayList<Double>();
    
    // store S1 space capacity,the 3rd column of data 
    private ArrayList<Double> s1Capacity = new ArrayList<Double>();
    
    // store S0 space usage,the 4th column of data 
    private ArrayList<Double> s0Usage = new ArrayList<Double>();
    
    // store S1 space usage,the 5th column of data 
    private ArrayList<Double> s1Usage = new ArrayList<Double>();
    
    // store eden space capacity,the 6th column of data 
    private ArrayList<Double> edenCapacity = new ArrayList<Double>();
    
    // store eden space usage,the 7th column of data
    private ArrayList<Double> edenUsage = new ArrayList<Double>();
    
    // store old generation capacity,the 8th column of data
    private ArrayList<Double> oldCapacity = new ArrayList<Double>();
    
    // store old generation usage,the 9th column of data 
    private ArrayList<Double> oldUsage = new ArrayList<Double>();
    
    // store meta space capacity,the 10th column of data 
    private ArrayList<Double> metaCapacity = new ArrayList<Double>();
    
    // store meta space usage,the 11th column of data
    private ArrayList<Double> metaUsage = new ArrayList<Double>();
    
    // an array to store last 5 columns data in data line
    private double[] GCInfo = new double[5];
    
    /**
     * Constructor
     * 
     * @param pid current instance represents the GC data for this pid
     */
    public DataWrapper(String pid)
    {
        this.pid = pid; // set pid
        dataLines = new DataSource(pid).getDataLines();// get the data list
    }
    
    /**
     * Add formatted data to ArrayList
     */
    public void addDataToList()
    {
        // All data in the dataLine has been processed
        // waiting for the save data thread to add data to the dataLine.
        while (dataLines.size() == index)
        {
            // threads has been terminated, without more data
            if (!Tools.isThreadRunning(pid))
                return;
        }
        if (Thread.currentThread().isInterrupted())
            return;// jump out when current SwingWorker is cancelled
        int size = dataLines.size();
        // add new data to ArrayList
        for (int i = index; i < size; i++)
        {
            // split data line with ","
            String[] data = dataLines.get(i).split(",");
            // add date
            timeList.add(new Date(Long.parseLong(data[0])));
            // add heap capacity,unit:MB
            heapCapacity.add((Double.parseDouble(data[1]) + Double.parseDouble(data[2])
                + Double.parseDouble(data[5]) + Double.parseDouble(data[7])) / 1024);
            // add heap usage,unit:MB
            heapUsage.add((Double.parseDouble(data[3]) + Double.parseDouble(data[4])
                + Double.parseDouble(data[6]) + Double.parseDouble(data[8])) / 1024);
            
            s0Capacity.add(Double.parseDouble(data[1]) / 1024);// add S0 capacity,unit:MB
            s1Capacity.add(Double.parseDouble(data[2]) / 1024);// add S1 capacity,unit:MB
            s0Usage.add(Double.parseDouble(data[3]) / 1024);// add S0 usage,unit:MB
            s1Usage.add(Double.parseDouble(data[4]) / 1024);// add S1 usage,unit:MB
            edenCapacity.add(Double.parseDouble(data[5]) / 1024);// add eden capacity,unit:MB
            edenUsage.add(Double.parseDouble(data[6]) / 1024);// add eden usage,unit:MB
            oldCapacity.add(Double.parseDouble(data[7]) / 1024);// add old generation capacity,unit:MB
            oldUsage.add(Double.parseDouble(data[8]) / 1024);// add old generation usage,unit:MB
            metaCapacity.add(Double.parseDouble(data[9]) / 1024);// add meta space capacity,unit:MB
            metaUsage.add(Double.parseDouble(data[10]) / 1024);// add meta space usage,unit:MB
            GCInfo[0] = Double.parseDouble(data[13]);// add the count of minor GC
            GCInfo[1] = Double.parseDouble(data[14]);// add the time spent by minor GC
            GCInfo[2] = Double.parseDouble(data[15]);// add the count of full GC
            GCInfo[3] = Double.parseDouble(data[16]);// add the time spent by full GC
            GCInfo[4] = Double.parseDouble(data[17]);// add the total time spent by GC
        }
        // Unprocessed data lines start from size
        index = size;
    }
    
    /**
     * 
     * Returns a collection of all the data types needed for a chart
     * 
     */
    public List[] getDataList()
    {
        return new ArrayList[] {timeList, heapCapacity, heapUsage, s0Capacity, s0Usage,
            s1Capacity, s1Usage, edenCapacity, edenUsage, oldCapacity, oldUsage,
            metaCapacity, metaUsage};
    }
    
    /**
     * Return GC data array
     */
    public double[] getGCInfo()
    {
        return GCInfo;
    }
}
