package myGCtool;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;

import org.junit.Test;

public class ProcessDataTest
{
    @Test
    public void testGetProcesses()
    {
        // at least current test process is running
        assertTrue(new ProcessData().getProcesses().length > 0);
    }
    
    @Test
    public void testIsProcessRunning()
    {
        ProcessData data = new ProcessData();
        // Random a pid, the process is not running
        assertFalse(data.isProcessRunning("123"));
        // current test process is running
        assertTrue(data.isProcessRunning(
            ManagementFactory.getRuntimeMXBean().getName().split("@")[0]));
    }
    
}
