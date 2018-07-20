package myGCtool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataWrapper
{
    private List<String> dataLines;
    
    private int index = 0;
    
    private ArrayList<Date> timeList = new ArrayList<Date>();
    
    private ArrayList<Double> heapCapacity = new ArrayList<Double>();
    
    private ArrayList<Double> heapUsage = new ArrayList<Double>();
    
    private ArrayList<Double> s0Usage = new ArrayList<Double>();
    
    private ArrayList<Double> s0Capacity = new ArrayList<Double>();
    
    private ArrayList<Double> s1Usage = new ArrayList<Double>();
    
    private ArrayList<Double> s1Capacity = new ArrayList<Double>();
    
    private ArrayList<Double> edenUsage = new ArrayList<Double>();
    
    private ArrayList<Double> edenCapacity = new ArrayList<Double>();
    
    private ArrayList<Double> oldCapacity = new ArrayList<Double>();
    
    private ArrayList<Double> oldUsage = new ArrayList<Double>();
    
    private ArrayList<Double> metaCapacity = new ArrayList<Double>();
    
    private ArrayList<Double> metaUsage = new ArrayList<Double>();
    
    public DataWrapper(String pid)
    {
        DataSource util = new DataSource();
        new Thread(() -> util.writeData(pid), pid + "writeThread").start();
        try
        {
            Thread.sleep(300);
        }
        catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }
        
        new Thread(() -> util.readData(pid), pid + "readThread").start();
        dataLines = util.getDataLines();
    }
    
    public void setDataList()
    {
        while (dataLines.size() == index)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                return;
            }
        }
        int size = dataLines.size();
        for (int i = index; i < size; i++)
        {
            String[] data = dataLines.get(i).split(",");
            timeList.add(new Date(Long.parseLong(data[0])));
            heapCapacity.add((Double.parseDouble(data[1]) + Double.parseDouble(data[2])
                + Double.parseDouble(data[5]) + Double.parseDouble(data[7])) / 1024.0);
            heapUsage.add((Double.parseDouble(data[3]) + Double.parseDouble(data[4])
                + Double.parseDouble(data[6]) + Double.parseDouble(data[8])) / 1024.0);
            
            s0Capacity.add(Double.parseDouble(data[1]) / 1024.0);
            s1Capacity.add(Double.parseDouble(data[2]) / 1024.0);
            s0Usage.add(Double.parseDouble(data[3]) / 1024.0);
            s1Usage.add(Double.parseDouble(data[4]) / 1024.0);
            edenCapacity.add(Double.parseDouble(data[5]) / 1024.0);
            edenUsage.add(Double.parseDouble(data[6]) / 1024.0);
            oldCapacity.add(Double.parseDouble(data[7]) / 1024.0);
            oldUsage.add(Double.parseDouble(data[8]) / 1024.0);
            metaCapacity.add(Double.parseDouble(data[9]) / 1024.0);
            metaUsage.add(Double.parseDouble(data[10]) / 1024.0);
            
        }
        index = size;
    }
    
    public List[] getDataList()
    {
        return new ArrayList[] {timeList, heapCapacity, heapUsage, s0Capacity, s0Usage,
            s1Capacity, s1Usage, edenCapacity, edenUsage, oldCapacity, oldUsage,
            metaCapacity, metaUsage};
    }
}
