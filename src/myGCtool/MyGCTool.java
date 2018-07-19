package myGCtool;

import java.io.File;

public class MyGCTool
{
    public static void main(String[] args)
    {
        deleteCSVFile();
        new ConnectionFrame("Connection");
    }
    
    private static void deleteCSVFile()
    {
        String path = System.getProperty("user.dir");
        File dir = new File(path);
        File[] files = dir.listFiles();
        
        for (File file : files)
        {
            if (file.getName().endsWith(".csv"))
            {
                file.delete();
            }
        }
    }
}
