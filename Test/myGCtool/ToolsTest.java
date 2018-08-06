package myGCtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ToolsTest
{
    
    @Test
    public void testGetDataFileName()
    {
        assertEquals(ManagementFactory.getRuntimeMXBean().getName().split("@")[0] + "_"
            + "123.csv", Tools.getDataFileName("123"));
        
        assertNotEquals("123", Tools.getDataFileName("123"));
    }
    
    @Test
    public void testDeleteCSVFile()
        throws IOException
    {
        File dir = new File("temp");
        if (!dir.exists())
            dir.mkdir();
        File file = new File("temp",
            ManagementFactory.getRuntimeMXBean().getName().split("@")[0] + "_123.csv");
        file.createNewFile();
        assertTrue(file.exists());
        List<String> pids = new ArrayList<>();
        pids.add("123");
        Tools.deleteCSVFile(pids);
        assertFalse(file.exists());
    }
    
    @Test
    public void testGetProcesses()
    {
        assertTrue(Tools.getProcesses().length > 0);
    }
    
    @Test
    public void testIsThreadRunning()
    {
        assertFalse(Tools.isThreadRunning("123"));
        Thread thread = new Thread("456writeThread")
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(50);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        assertTrue(Tools.isThreadRunning("456"));
    }
    
    @Test
    public void testIsProcessRunning()
    {
        assertFalse(Tools.isProcessRunning("123"));
        assertTrue(Tools.isProcessRunning(
            ManagementFactory.getRuntimeMXBean().getName().split("@")[0]));
    }
    
}
