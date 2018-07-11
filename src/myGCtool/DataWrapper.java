package myGCtool;

import java.io.IOException;
import java.util.List;

public class DataWrapper
{
    private List<String> dataLines;
    
    public DataWrapper(String pid)
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
    
    public double[][] getHeapUsage(int phase)
    {
        double[] time = new double[5];
        double[] capacity = new double[5];
        double[] usage = new double[5];
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
            time[i - 5 * phase] = i;
            capacity[i - 5 * phase] =
                (Double.parseDouble(data[1]) + Double.parseDouble(data[2])
                    + Double.parseDouble(data[5]) + Double.parseDouble(data[7])) / 1024;
            usage[i - 5 * phase] =
                (Double.parseDouble(data[3]) + Double.parseDouble(data[4])
                    + Double.parseDouble(data[6]) + Double.parseDouble(data[8])) / 1024;
        }
        
        return new double[][] {time, capacity, usage};
    }
}
