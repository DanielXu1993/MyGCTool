package myGCtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Provides some static methods that are often used.
 */
public class Tools
{
    /**
     * private constructor to avoid being created instances
     */
    private Tools()
    {
    }
    
    /**
     * Get the process id of the currently running MyGCTool
     * 
     * @return process id of the currently running MyGCTool
     */
    public static String getCurrentProcessId()
    {
        // the name representing the running JVM : pid@name
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
    
    /**
     * Close read and write thread and delete the corresponding data file
     * according to the monitored pid
     * 
     * @param pids the monitored pid list
     */
    public static void deleteCSVFile(List<String> pids)
    {
        // Closing the related threads before delete data file
        closeThread(pids);
        for (String pid : pids)
        {
            // Corresponding data file under temp folder
            File file = new File("temp", getCurrentProcessId() + "_" + pid + ".csv");
            // File exists but delete failed
            while (file.exists() && !file.delete())
            {
                try
                {
                    // Waiting for the corresponding data thread to end
                    // Try to delete the file every 20 milliseconds
                    Thread.sleep(20);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        // delete the temp folder if it is empty
        File temp = new File("temp");
        if (temp.isDirectory() && temp.list().length == 0)
            temp.delete();
    }
    
    /**
     * Interrupt the read and write data threads of the specified pid
     * 
     * @param pids the pid list
     */
    private static void closeThread(List<String> pids)
    {
        // Get all threads
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread thread : set)
        {
            for (String pid : pids)
            {
                // Send an interrupt instruction based on the thread name
                // thread name : "<pid>writeThread" and "<pid>readThread"
                if (thread.getName().equals(pid + "writeThread")
                    || thread.getName().equals(pid + "readThread"))
                    thread.interrupt();
            }
        }
    }
    
    /**
     * Determine whether the read/write process is running according to pid
     * 
     * @param pid the pid
     * @return true the threads are running otherwise false
     */
    public static boolean isThreadRunning(String pid)
    {
        // get all running threads
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread thread : set)
        {
            // whether contains the read/write process
            if (thread.getName().equals(pid + "writeThread")
                || thread.getName().equals(pid + "readThread"))
                return true; // contains the process
        }
        return false; // does not contain the process
    }
    
    /**
     * Get the current process information that can be monitored
     * 
     * @return A 2D array whose first column is pid and the second column is the process name.
     */
    public static String[][] getProcesses()
    {
        // get process info map
        Map<String, String> apps = getProcInfo();
        // the 2D array used to store the data
        String[][] strs = new String[apps.size()][2];
        // get map key set
        Set<Entry<String, String>> entries = apps.entrySet();
        // the row index of the 2D array
        int index = 0;
        for (Entry<String, String> entry : entries)
        {
            // add a 1D array to the 2D array
            // 1D array : [pid,name]
            strs[index] = new String[] {entry.getKey(), entry.getValue()};
            index++;
        }
        
        return strs;
    }
    
    /**
     * Get information about all java processes.
     * 
     * @return map which key the pid and the value is the name
     */
    private static Map<String, String> getProcInfo()
    {
        // the map to store the data
        Map<String, String> apps = new HashMap<>();
        BufferedReader reader = null;
        try
        {
            try
            {
                // call command "jps" to get all running java processes
                Process exec = Runtime.getRuntime().exec("jps");
                // read result from "jps"
                reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                String line = null;
                // read result
                while ((line = reader.readLine()) != null)
                {
                    // split data with ""
                    String[] strs = line.split(" ");
                    // pid and name are not null
                    if (strs.length == 2)
                        apps.put(strs[0], strs[1]);
                    else // name is null
                        apps.put(strs[0], "");
                }
            }
            finally
            {
                if (reader != null)
                    reader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return apps;
    }
    
    /**
     * Whether the monitored java process is running
     * 
     * @param pid monitored java process id
     * @return true the process is running otherwise false
     */
    public static boolean isProcessRunning(String pid)
    {
        // true if the pid is in the process map otherwise false
        return getProcInfo().containsKey(pid);
    }
    
    /**
     * Perform a full GC on the monitored java processes
     * 
     * @param pids the pid list of all monitored processes
     */
    public static void performGC(List<String> pids)
    {
        for (String pid : pids)
        {
            try
            {
                // execute command "jcmd <pid> GC.run" to perform a full GC
                Runtime.getRuntime().exec("jcmd " + pid + " GC.run");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
