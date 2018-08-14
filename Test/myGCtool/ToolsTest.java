package myGCtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;

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
    public void testIsThreadRunning()
    {
        assertFalse(Tools.isThreadRunning("123"));
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
        assertTrue(Tools.isThreadRunning("456"));
    }
    
}
