package myGCtool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataWrapper
{
    private List<String> dataLines;
    
    public DataWrapper(int pid)
    {
        Util util = new Util();
        new Thread(() -> util.writeData(pid)).start();
        
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }
        
        new Thread(() -> {
            try
            {
                util.readData(pid);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }).start();
        dataLines = util.getDataLines();
    }
    
    private List<Date> timeList = new ArrayList<Date>();
    
    private List<Double> capacityList = new ArrayList<Double>();
    
    private List<Double> usageList = new ArrayList<Double>();
    
    public List[] getHeapUsage(int phase)
    {
        while (dataLines.size() < 5 + 5 * phase)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        for (int i = 5 * phase; i < 5 + 5 * phase; i++)
        {
            String[] data = dataLines.get(i).split(",");
            timeList.add(new Date(Long.parseLong(data[0])));
            capacityList.add((Double.parseDouble(data[1]) + Double.parseDouble(data[2])
                + Double.parseDouble(data[5]) + Double.parseDouble(data[7])) / 1024);
            usageList.add((Double.parseDouble(data[3]) + Double.parseDouble(data[4])
                + Double.parseDouble(data[6]) + Double.parseDouble(data[8])) / 1024);
        }
        
        return new ArrayList[] {(ArrayList)timeList, (ArrayList)capacityList,
            (ArrayList)usageList};
    }
}
