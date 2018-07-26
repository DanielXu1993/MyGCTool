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

public class DataSource
{
    private List<String> dataLines = new ArrayList<>();
    
    private Process exec;
    
    private String pid;
    
    private String fileName;
    
    public DataSource(String pid)
    {
        this.pid = pid;
        this.fileName = Tools.getCurrentProcessId() + "_" + pid + ".csv";
    }
    
    public void writeData()
    {
        
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try
        {
            try
            {
                exec = Runtime.getRuntime().exec("jstat -gc " + pid + " 1000");
                reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                File temp = new File("temp");
                if (!temp.exists() || !temp.isDirectory())
                    temp.mkdir();
                File file = new File("temp", fileName);
                writer = new BufferedWriter(new FileWriter(file));
                Thread.sleep(100);
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
                                lines.append(strs[i] + ",");
                        }
                        lines.deleteCharAt(lines.length() - 1);
                        writer.write(lines.toString());
                        writer.newLine();
                        writer.flush();
                    }
                    index++;
                    if (Thread.currentThread().isInterrupted())
                        break;
                }
                exec.destroy();
            }
            
            finally
            {
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
            }
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
    
    public void readData()
    {
        RandomAccessFile reader = null;
        String line = null;
        try
        {
            try
            {
                reader = new RandomAccessFile(new File("temp", fileName), "r");
                while (true)
                {
                    while ((line = reader.readLine()) != null)
                    {
                        dataLines.add(line);
                    }
                    reader.seek(reader.length());
                    if (Thread.currentThread().isInterrupted())
                        break;
                    if (!exec.isAlive())
                        break;
                }
            }
            
            finally
            {
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
    }
    
    public List<String> getDataLines()
    {
        return dataLines;
    }
    
}
