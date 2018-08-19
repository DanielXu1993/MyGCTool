package myGCtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ToolsTest
{
    
    @Test
    public void testGetDataFileName()
    {
        // the format of the name of the data file should be "<Tool process id>_<monitored process id>.csv"
        assertEquals(ManagementFactory.getRuntimeMXBean().getName().split("@")[0] + "_"
            + "123.csv", Tools.getDataFileName("123"));
        
        assertNotEquals("123", Tools.getDataFileName("123"));
    }
    
    @Test
    public void testIsThreadRunning()
    {
        // Random a pid, the thread "123saveThread" is not running
        assertFalse(Tools.isThreadRunning("123"));
        // start a thread with name "456saveThread"
        Thread thread = new Thread("456saveThread")
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
        // the thread should be running
        assertTrue(Tools.isThreadRunning("456"));
    }
    
    @Test
    public void testCloseThread()
        throws InterruptedException
    {
        // start a thread with name "456saveThread"
        Thread thread = new Thread("456saveThread")
        {
            // a long time task
            @Override
            public void run()
            {
                // do a task until the thread is interrupted
                while (!Thread.currentThread().isInterrupted())
                {
                    
                }
            }
        };
        thread.start();
        // the thead 456saveThread should be running
        assertTrue(Tools.isThreadRunning("456"));
        List<String> pids = new ArrayList<>();
        pids.add("456");
        // close the 456saveThread thread
        Tools.closeThread(pids);
        // the thread should be closed
        assertFalse(Tools.isThreadRunning("456"));
    }
}
