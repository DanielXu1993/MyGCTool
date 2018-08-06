package myGCtool;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class DataWrapperTest
{
    
    @Test
    public void testAddDataToListAndGetDataList()
        throws Exception
    {
        assertEquals(0, new DataWrapper("123").getDataList()[0].size());
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        DataWrapper wrapper = new DataWrapper(pid);
        Thread.sleep((new Random().nextInt(10) + 1) * 1000);
        BufferedReader reader = new BufferedReader(
            new FileReader(new File("temp", Tools.getDataFileName(pid))));
        String line = null;
        int size = 0;
        while ((line = reader.readLine()) != null)
        {
            size++;
        }
        reader.close();
        wrapper.addDataToList();
        assertEquals(size, wrapper.getDataList()[0].size());
    }
    
    @Test
    public void testGetGCInfo()
        throws Exception
    {
        assertArrayEquals(new double[] {0.0, 0.0, 0.0, 0.0, 0.0},
            new DataWrapper("123").getGCInfo(), 0.0);
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        DataWrapper wrapper = new DataWrapper(pid);
        Thread.sleep((new Random().nextInt(10) + 1) * 1000);
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
        double[] actual = new double[5];
        String[] strs = line.split(",");
        int index = 0;
        for (int i = strs.length - 5; i < strs.length; i++)
        {
            actual[index] = Double.parseDouble(strs[i]);
            index++;
        }
        assertArrayEquals(expected, actual, 0.0);
    }
    
    @Before
    public void deleteFile()
        throws Exception
    {
        File dir = new File("temp");
        if (dir.exists() && dir.isDirectory())
        {
            File[] files = dir.listFiles();
            for (File file : files)
                file.delete();
            dir.delete();
        }
    }
}
