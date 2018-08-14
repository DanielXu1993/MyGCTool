package myGCtool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class encapsulates methods to get all processes information that can be monitored.
 */
public class ProcessData
{
    /**
     * Get the current process information that can be monitored
     * 
     * @return A 2D array whose first column is pid and the second column is the process name.
     */
    public String[][] getProcesses()
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
    private Map<String, String> getProcInfo()
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
    public boolean isProcessRunning(String pid)
    {
        // true if the pid is in the process map otherwise false
        return getProcInfo().containsKey(pid);
    }
}
