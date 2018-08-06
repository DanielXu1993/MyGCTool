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
        assertNull(new HistogramData().rowData("123"));
        
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        assertEquals(11, new HistogramData().rowData(pid).length);
    }
    
}
