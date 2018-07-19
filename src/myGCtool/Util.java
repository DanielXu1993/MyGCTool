package myGCtool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Util
{
    private List<String> dataLines = new ArrayList<>();
    
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
                    lines.append(System.currentTimeMillis() + ",");
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
    
    public void readData(String pid)
        throws IOException
    {
        RandomAccessFile reader = new RandomAccessFile(pid + ".csv", "r");
        String line = null;
        while (true)
        {
            while ((line = reader.readLine()) != null)
            {
                dataLines.add(line);
            }
            reader.seek(reader.length());
        }
    }
    
    public List<String> getDataLines()
    {
        return dataLines;
    }
}
