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
    
    private List<String> dataLines;// data lines from data file
    
    // The index of the beginning of data line to be processed next time in dataLines
    private int index = 0;
    
    /**
     * The following ArrayList stores different types of data from data file.
     * Their size is the same. The data under the index represents the index row in the data file.
     */
    // store time, the first column of data in the data file
    private ArrayList<Date> timeList = new ArrayList<Date>();
    
    // store heap capacity, the total of the s0 capacity,s1 capacity,eden capacity and old generation capacity
    private ArrayList<Integer> heapCapacity = new ArrayList<>();
    
    // store heap usage, the total of the s0 usage,s1 usage,eden usage and old generation usage
    private ArrayList<Integer> heapUsage = new ArrayList<>();
    
    // store S0 space capacity,the second column of data in the data file
    private ArrayList<Integer> s0Capacity = new ArrayList<Integer>();
    
    // store S1 space capacity,the 3rd column of data in the data file
    private ArrayList<Integer> s1Capacity = new ArrayList<Integer>();
    
    // store S0 space usage,the 4th column of data in the data file
    private ArrayList<Integer> s0Usage = new ArrayList<Integer>();
    
    // store S1 space usage,the 5th column of data in the data file
    private ArrayList<Integer> s1Usage = new ArrayList<Integer>();
    
    // store eden space capacity,the 6th column of data in the data file
    private ArrayList<Integer> edenCapacity = new ArrayList<Integer>();
    
    // store eden space usage,the 7th column of data in the data file
    private ArrayList<Integer> edenUsage = new ArrayList<Integer>();
    
    // store old generation capacity,the 8th column of data in the data file
    private ArrayList<Integer> oldCapacity = new ArrayList<Integer>();
    
    // store old generation usage,the 9th column of data in the data file
    private ArrayList<Integer> oldUsage = new ArrayList<Integer>();
    
    // store meta space capacity,the 10th column of data in the data file
    private ArrayList<Integer> metaCapacity = new ArrayList<Integer>();
    
    // store meta space usage,the 11th column of data in the data file
    private ArrayList<Integer> metaUsage = new ArrayList<Integer>();
    
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
        DataSource dataSource = new DataSource(pid); // to get data from DataSource
        // start a thread to write data to data file and set the thread name to "<pid>writeThread"
        new Thread(() -> dataSource.writeData(), pid + "writeThread").start();
        // start a thread to read data from data file and set the thread name to "<pid>readThread"
        new Thread(() -> dataSource.readData(), pid + "readThread").start();
        dataLines = dataSource.getDataLines();// get the data list
    }
    
    /**
     * Add formatted data to ArrayList
     * Synchronized method ensures that no data interference occurs when monitoring multiple processes.
     */
    public synchronized void addDataToList()
    {
        // All data in the dataLine has been processed
        // waiting for the read thread to add data to the dataLine.
        while (dataLines.size() == index)
        {
            // threads has been terminated, without more data
            if (!Tools.isThreadRunning(pid))
                return;
        }
        int size = dataLines.size();
        // add new data to ArrayList
        for (int i = index; i < size; i++)
        {
            // split data line with ","
            String[] data = dataLines.get(i).split(",");
            // add date
            timeList.add(new Date(Long.parseLong(data[0])));
            // add heap capacity,unit:MB
            heapCapacity
                .add((int)(Double.parseDouble(data[1]) + Double.parseDouble(data[2])
                    + Double.parseDouble(data[5]) + Double.parseDouble(data[7])) / 1024);
            // add heap usage,unit:MB
            heapUsage.add((int)(Double.parseDouble(data[3]) + Double.parseDouble(data[4])
                + Double.parseDouble(data[6]) + Double.parseDouble(data[8])) / 1024);
            
            s0Capacity.add((int)Double.parseDouble(data[1]) / 1024);// add S0 capacity,unit:MB
            s1Capacity.add((int)Double.parseDouble(data[2]) / 1024);// add S1 capacity,unit:MB
            s0Usage.add((int)Double.parseDouble(data[3]) / 1024);// add S0 usage,unit:MB
            s1Usage.add((int)Double.parseDouble(data[4]) / 1024);// add S1 usage,unit:MB
            edenCapacity.add((int)Double.parseDouble(data[5]) / 1024);// add eden capacity,unit:MB
            edenUsage.add((int)Double.parseDouble(data[6]) / 1024);// add eden usage,unit:MB
            oldCapacity.add((int)Double.parseDouble(data[7]) / 1024);// add old generation capacity,unit:MB
            oldUsage.add((int)Double.parseDouble(data[8]) / 1024);// add old generation usage,unit:MB
            metaCapacity.add((int)Double.parseDouble(data[9]) / 1024);// add meta space capacity,unit:MB
            metaUsage.add((int)Double.parseDouble(data[10]) / 1024);// add meta space usage,unit:MB
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
