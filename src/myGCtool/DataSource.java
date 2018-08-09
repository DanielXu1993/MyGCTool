package myGCtool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates methods to get GC data from the currently monitored process and save these data.
 */
public class DataSource
{
    private Process exec; // process to execute the command
    
    private String pid; // currently monitored process id
    
    private List<String> dataLines = new ArrayList<>(); // store data lines from jstat
    
    /**
     * Constructor
     * 
     * @param pid currently monitored process id
     */
    public DataSource(String pid)
    {
        this.pid = pid;// set pid
        // start a thread named "<pid>writeThread" to read data from jstat and
        // save data to the dataLines list
        new Thread(() -> saveData(), pid + "saveThread").start();
    }
    
    /**
     * 
     * read GC data from jstat tool and store data to dataLines
     * 
     */
    private void saveData()
    {
        
        BufferedReader reader = null;
        try
        {
            try
            {
                // execute command " jstat -gc <pid> 1000"
                // get GC data every 1000 milliseconds
                exec = Runtime.getRuntime().exec("jstat -gc " + pid + " 1000");
                // get data from inputstream
                reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                String line = null;// data line
                int index = 0; // data line index
                // save data to dataLines list
                while ((line = reader.readLine()) != null)
                {
                    // do not read first line
                    if (index != 0)
                    {
                        // Split data by " "
                        String[] strs = line.split(" ");
                        // Data lines that match the format
                        StringBuilder lines = new StringBuilder();
                        // Start with current time in milliseconds
                        lines.append(System.currentTimeMillis() + ",");
                        // Insert valid data
                        for (int i = 0; i < strs.length; i++)
                        {
                            // Avoid spaces.
                            if (!"".equals(strs[i]))
                                // Data is separated by ","
                                lines.append(strs[i] + ",");
                        }
                        // Delete the last ","
                        lines.deleteCharAt(lines.length() - 1);
                        // add data line to the list
                        getDataLines().add(lines.toString());
                    }
                    index++; // ine index increases by 1
                    if (Thread.currentThread().isInterrupted())
                        break; // jump out of the loop when thread is interrupted
                }
            }
            
            finally
            {
                // close read stream
                if (reader != null)
                    reader.close();
                if (exec != null)
                    exec.destroy(); // destroy this command task
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * return dataLines
     */
    public List<String> getDataLines()
    {
        return dataLines;
    }
    
}
