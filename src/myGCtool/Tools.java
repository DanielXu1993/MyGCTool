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

public class Tools
{
    public static String getCurrentProcessId()
    {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
    
    public static void deleteCSVFile(List<String> pids)
    {
        for (String pid : pids)
        {
            File file = new File("temp", getCurrentProcessId() + "_" + pid + ".csv");
            while (file.exists() && !file.delete())
            {
                try
                {
                    Thread.sleep(20);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        File temp = new File("temp");
        if (temp.isDirectory() && temp.list().length == 0)
            temp.delete();
    }
    
    public static void closeThread(List<String> pids)
    {
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread thread : set)
        {
            for (String pid : pids)
            {
                if (thread.getName().equals(pid + "writeThread")
                    || thread.getName().equals(pid + "readThread"))
                    thread.interrupt();
            }
        }
    }
    
    public static boolean isThreadRunning(String pid)
    {
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread thread : set)
        {
            if (thread.getName().equals(pid + "writeThread")
                || thread.getName().equals(pid + "readThread"))
                return true;
        }
        return false;
    }
    
    public static String[][] getProcesses()
    {
        Map<String, String> apps = getProcInfo();
        String[][] strs = new String[apps.size()][2];
        Set<Entry<String, String>> entries = apps.entrySet();
        int index = 0;
        for (Entry<String, String> entry : entries)
        {
            strs[index] = new String[] {entry.getKey(), entry.getValue()};
            index++;
        }
        
        return strs;
    }
    
    private static Map<String, String> getProcInfo()
    {
        Map<String, String> apps = new HashMap<>();
        BufferedReader reader = null;
        try
        {
            try
            {
                Process exec = Runtime.getRuntime().exec("jps");
                reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    String[] strs = line.split(" ");
                    apps.put(strs[0], strs[1]);
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
    
    public static boolean isProcessRunning(String pid)
    {
        return getProcInfo().get(pid) != null;
    }
    
    public static void performGC(List<String> pids)
    {
        for (String pid : pids)
        {
            try
            {
                Runtime.getRuntime().exec("jcmd " + pid + " GC.run");
            }
            catch (IOException e)
            {
                continue;
            }
        }
    }
}
