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

import org.junit.Before;
import org.junit.Test;

public class DataSourceTest
{
    @Test
    public void testGetDataLines()
        throws Exception
    {
        assertEquals(0, new DataSource("123").getDataLines().size());
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        DataSource source = new DataSource(pid);
        Thread.sleep((new Random().nextInt(10) + 1) * 1000);
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
        assertArrayEquals(source.getDataLines().toArray(), dataLine.toArray());
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
