package myGCtool;

import java.io.File;
import java.util.List;
import java.util.Set;

public class Tools
{
    public static void deleteCSVFile(List<String> pids)
    {
        String path = System.getProperty("user.dir");
        for (String pid : pids)
        {
            File file = new File(path + "\\" + pid + ".csv");
            while (!file.delete())
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
                {
                    thread.interrupt();
                }
            }
        }
    }
    
    public static boolean isRunning(String pid)
    {
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread thread : set)
        {
            if (thread.getName().equals(pid + "writeThread")
                || thread.getName().equals(pid + "readThread"))
            {
                return true;
            }
        }
        return false;
    }
}
