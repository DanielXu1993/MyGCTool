package myGCtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.management.ManagementFactory;

import org.junit.Test;

public class HistogramDataTest
{
    
    @Test
    public void testRowData()
    {
        // Random a pid, no heap histogram data
        assertNull(new HistogramData().rowData("123"));
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        // current test process data (top 10 objects and the last line of the output)
        assertEquals(11, new HistogramData().rowData(pid).length);
    }
    
}
