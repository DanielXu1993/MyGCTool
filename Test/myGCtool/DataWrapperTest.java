package myGCtool;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Test;

public class DataWrapperTest
{
    
    @Test
    public void testAddDataToListAndGetDataList()
        throws Exception
    {
        // Random a pid, no data
        assertEquals(0, new DataWrapper("123").getDataList()[0].size());
        // get current test process id
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        DataWrapper wrapper = new DataWrapper(pid);
        // Keep the current thread for a while
        Thread.sleep((new Random().nextInt(10) + 1) * 1000);
        List<String> pids = new ArrayList<>();
        pids.add(pid);
        // stop writing data to the data file
        Tools.closeThread(pids);
        // get the count of data lines from data file
        BufferedReader reader = new BufferedReader(
            new FileReader(new File("temp", Tools.getDataFileName(pid))));
        String line = null;
        int size = 0;
        reader.readLine();
        while ((line = reader.readLine()) != null)
        {
            size++;
        }
        reader.close();
        wrapper.addDataToList();
        List[] lists = wrapper.getDataList();
        // the count of data from data file and the data in dataLine should be the same
        assertEquals(size, lists[(new Random().nextInt(lists.length))].size());
    }
    
    @Test
    public void testGetGCInfo()
        throws Exception
    {
        // Random a pid, GC infor should be {0.0, 0.0, 0.0, 0.0, 0.0}
        assertArrayEquals(new double[] {0.0, 0.0, 0.0, 0.0, 0.0},
            new DataWrapper("123").getGCInfo(), 0.0);
        // get current test process id
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        DataWrapper wrapper = new DataWrapper(pid);
        Thread.sleep((new Random().nextInt(10) + 1) * 1000);
        List<String> pids = new ArrayList<>();
        pids.add(pid);
        // stop writing data to the data file
        Tools.closeThread(pids);
        // get last line of data from data file
        BufferedReader reader = new BufferedReader(
            new FileReader(new File("temp", Tools.getDataFileName(pid))));
        String line = null;
        String temp = null;
        while ((temp = reader.readLine()) != null)
        {
            line = temp;
        }
        reader.close();
        wrapper.addDataToList();
        double[] expected = wrapper.getGCInfo();
        // data from data file is the actual data
        double[] actual = new double[5];
        // convert the string to double array
        String[] strs = line.split(",");
        int index = 0;
        for (int i = strs.length - 5; i < strs.length; i++)
        {
            actual[index] = Double.parseDouble(strs[i]);
            index++;
        }
        // the expected data should be the same with the data from last line of data file
        assertArrayEquals(expected, actual, 0.0);
    }
    
    // delete test data file
    @After
    public void deleteFile()
    {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        new File("temp", pid + "_123.csv").delete();
        while (!new File("temp", pid + "_" + pid + ".csv").delete())
        {
        }
    }
}
