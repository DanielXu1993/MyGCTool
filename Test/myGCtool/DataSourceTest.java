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

public class DataSourceTest
{
    @Test
    public void testGetDataLines()
        throws Exception
    {
        // Random a pid, no data
        assertEquals(0, new DataSource("123").getDataLines().size());
        // get current test process id
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        DataSource source = new DataSource(pid);
        // Keep the current thread for a while
        Thread.sleep((new Random().nextInt(10) + 1) * 1000);
        List<String> pids = new ArrayList<>();
        pids.add(pid);
        // stop writing data to the data file
        Tools.closeThread(pids);
        // get monitored process data from data file
        List<String> dataLine = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
            new FileReader(new File("temp", Tools.getDataFileName(pid))));
        String line = null;
        reader.readLine();
        while ((line = reader.readLine()) != null)
        {
            dataLine.add(line);
        }
        reader.close();
        // the data from data file and the data in dataLine should be the same
        assertArrayEquals(source.getDataLines().toArray(), dataLine.toArray());
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
