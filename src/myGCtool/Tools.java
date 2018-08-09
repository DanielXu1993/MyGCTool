package myGCtool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
     * Interrupt the save data threads of the specified pid
     * 
     * @param pids the pid list
     */
    public static void closeThread(List<String> pids)
    {
        // Get all threads
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread thread : set)
        {
            for (String pid : pids)
            {
                // Send an interrupt instruction based on the thread name
                // thread name : "<pid>saveThread"
                if (thread.getName().equals(pid + "saveThread"))
                    thread.interrupt();
            }
        }
    }
    
    /**
     * Determine whether the save data thread is running according to pid
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
            // whether contains the save data thread
            if (thread.getName().equals(pid + "saveThread"))
                return thread.isAlive();// whether the thread is running
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
    
}
