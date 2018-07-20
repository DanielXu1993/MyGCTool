package myGCtool;

import java.io.File;
import java.util.Set;

public class Tools
{
    public static void deleteCSVFile(String pid)
    {
        String path = System.getProperty("user.dir");
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
    
    public static void closeThread(String pid)
    {
        Set<Thread> set = Thread.getAllStackTraces().keySet();
        for (Thread thread : set)
        {
            if (thread.getName().equals(pid + "writeThread")
                || thread.getName().equals(pid + "readThread"))
            {
                thread.interrupt();
            }
        }
    }
}
