package myGCtool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util
{
    private List<String> dataLines = new ArrayList<>();
    
    public Map<String, String> getApp()
    {
        Map<String, String> apps = new HashMap<>();
        BufferedReader reader = null;
        try
        {
            Process exec = Runtime.getRuntime().exec("jps");
            reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                String[] strs = line.split(" ");
                for (int i = 0; i < strs.length; i++)
                {
                    apps.put(strs[0], strs[1]);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return apps;
    }
    
    public void writeData(String pid)
    {
        
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try
        {
            Process exec = Runtime.getRuntime().exec("jstat -gc " + pid + " 1000");
            reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            writer = new BufferedWriter(new FileWriter(pid + ".csv"));
            String line = null;
            int index = 0;
            while ((line = reader.readLine()) != null)
            {
                if (index != 0)
                {
                    String[] strs = line.split(" ");
                    StringBuilder lines = new StringBuilder();
                    lines.append(currentTime() + ",");
                    for (int i = 0; i < strs.length; i++)
                    {
                        
                        if (!"".equals(strs[i]))
                        {
                            lines.append(strs[i] + ",");
                        }
                    }
                    lines.deleteCharAt(lines.length() - 1);
                    writer.write(lines.toString());
                    writer.newLine();
                    writer.flush();
                }
                index++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    private String currentTime()
    {
        String time = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now());
        return time;
    }
    
    public void readData(String pid)
        throws IOException
    {
        long length = 0;
        RandomAccessFile reader = new RandomAccessFile(pid + ".csv", "r");
        String line = null;
        while (true)
        {
            while ((line = reader.readLine()) != null)
            {
                dataLines.add(line);
            }
            reader.seek(length + reader.length());
        }
    }
    
    public List<String> getDataLines()
    {
        return dataLines;
    }
    
}
