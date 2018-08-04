package myGCtool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates methods to get GC data from the currently monitored process and save these data.
 */
public class DataSource
{
    private Process exec; // process to execute the command
    
    private String pid; // currently monitored process id
    
    private List<String> dataLines = new ArrayList<>(); // store data lines from data file
    
    /**
     * Constructor
     * 
     * @param pid currently monitored process id
     */
    public DataSource(String pid)
    {
        this.pid = pid;// set pid
    }
    
    /**
     * 
     * read GC data from jstat tool and store data to data file
     * 
     */
    public void writeData()
    {
        
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try
        {
            try
            {
                // execute command " jstat -gc <pid> 1000"
                // get GC data every 1000 milliseconds
                exec = Runtime.getRuntime().exec("jstat -gc " + pid + " 1000");
                // get data from inputstream
                reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                // Locate to the temp folder
                File temp = new File("temp");
                if (!temp.exists() || !temp.isDirectory())
                    temp.mkdir();// New temp folder if it doesn't exist
                File file = new File("temp", Tools.getDataFileName(pid));// Create data file
                writer = new BufferedWriter(new FileWriter(file));// used to write data
                String line = null;// data line
                int index = 0; // data line index
                // write data to data file
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
                        // Write data line to the file.
                        writer.write(lines.toString());
                        // Wrap.
                        writer.newLine();
                        // Write data line immediately
                        writer.flush();
                    }
                    index++; // ine index increases by 1
                    if (Thread.currentThread().isInterrupted())
                        break; // jump out of the loop when thread is interrupted
                }
            }
            
            finally
            {
                // close file stream
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
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
     * read data from data file and save data to dataLines collection
     */
    public void readData()
    {
        RandomAccessFile reader = null;
        String line = null; // current data line
        try
        {
            try
            {
                // get current data file
                File file = new File("temp", Tools.getDataFileName(pid));
                // data file does not exist, wait for write data thread
                while (!file.exists())
                {
                    Thread.sleep(50);
                }
                reader = new RandomAccessFile(file, "r");
                while (true)
                {
                    // read data line
                    while ((line = reader.readLine()) != null)
                    {
                        dataLines.add(line);// add data line to list
                    }
                    // Set the starting point of the next read data
                    // to the end point of this reading.
                    reader.seek(reader.length());
                    if (Thread.currentThread().isInterrupted())
                        break; // jump out of the loop when thread is interrupted
                    if (!exec.isAlive())
                        break; // without more data,exit
                }
            }
            finally
            {
                // close file stream
                if (reader != null)
                    reader.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
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
