package myGCtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        // -------------Test to close one thread----------------------
        // start a thread named 123saveThread
        new TestThread("123").start();
        // thread 123saveThread should be running
        assertTrue(Tools.isThreadRunning("123"));
        List<String> pids = new ArrayList<>();
        pids.add("123");
        // close the 123saveThread thread
        Tools.closeThread(pids);
        // the thread should be closed
        assertFalse(Tools.isThreadRunning("123"));
        pids.clear();// clear pids list
        // -------------Test to close the thread with the same name---------
        // start two threads named 123saveThread
        new TestThread("123").start();
        new TestThread("123").start();
        // thread 123saveThread should be running
        assertTrue(Tools.isThreadRunning("123"));
        pids.add("123");
        pids.add("123");
        // close the 123saveThread threads
        Tools.closeThread(pids);
        // the thread should be closed
        assertFalse(Tools.isThreadRunning("123"));
        pids.clear();// clear pids list
        // -------------Test to close multiple threads----------------------
        // Randomly constructs an array containing multiple threads
        TestThread[] threads = new TestThread[new Random().nextInt(10)];
        // Initialize thread array
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new TestThread(i + "");
            // Add the pid corresponding to the thread to the pids list
            pids.add(i + "");
        }
        // start all threads
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
        // all threads should be running
        for (int i = 0; i < threads.length; i++)
        {
            assertTrue(Tools.isThreadRunning(i + ""));
        }
        // close all threads
        Tools.closeThread(pids);
        // all threads should not be running
        for (int i = 0; i < threads.length; i++)
        {
            assertFalse(Tools.isThreadRunning(i + ""));
        }
    }
    
    // Custom thread
    private class TestThread extends Thread
    {
        private String pid;
        
        public TestThread(String pid)
        {
            // Use "<pid>saveThread" as thread name.
            super(pid + "saveThread");
        }
        
        @Override
        public void run()
        {
            // do a task until the thread is interrupted
            while (!Thread.currentThread().isInterrupted())
            {
                
            }
        }
    }
}
