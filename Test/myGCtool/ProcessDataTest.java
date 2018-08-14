package myGCtool;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;

import org.junit.Test;

public class ProcessDataTest
{
    @Test
    public void testGetProcesses()
    {
        assertTrue(new ProcessData().getProcesses().length > 0);
    }
    
    @Test
    public void testIsProcessRunning()
    {
        ProcessData data = new ProcessData();
        assertFalse(data.isProcessRunning("123"));
        assertTrue(data.isProcessRunning(
            ManagementFactory.getRuntimeMXBean().getName().split("@")[0]));
    }
    
}
