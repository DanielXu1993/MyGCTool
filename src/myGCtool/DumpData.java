package myGCtool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DumpData
{
    private List<String[]> addData(int count, String pid)
    {
        List<String[]> heapDataList = new ArrayList<>();
        Process exec = null;
        BufferedReader reader = null;
        try
        {
            try
            {
                exec = Runtime.getRuntime().exec("jmap -histo " + pid);
                reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                reader.readLine();
                reader.readLine();
                for (int i = 0; i < count; i++)
                {
                    String[] data = new String[4];
                    String line = reader.readLine();
                    String[] lineInfo = line.split("  ");
                    int index = 0;
                    for (int j = 0; j < lineInfo.length; j++)
                    {
                        if (!lineInfo[j].equals(""))
                        {
                            data[index] = lineInfo[j].trim();
                            index++;
                        }
                    }
                    heapDataList.add(data);
                }
                String lastLine = null;
                String temp = null;
                while ((temp = reader.readLine()) != null)
                {
                    lastLine = temp;
                }
                String[] strs = lastLine.split("  ");
                String[] totalData = new String[4];
                int index = 0;
                for (int i = 0; i < strs.length; i++)
                {
                    if (!strs[i].equals(""))
                    {
                        totalData[index] = strs[i].trim();
                        index++;
                    }
                }
                totalData[0] = "all objects in heap";
                totalData[3] = "";
                heapDataList.add(totalData);
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
        return heapDataList;
        
    }
    
    public String[][] rowData(String pid)
    {
        List<String[]> list = addData(10, pid);
        String[] total = list.get(list.size() - 1);
        Long totalInstances = Long.parseLong(total[1]);
        Long totalSize = Long.parseLong(total[2]);
        for (int i = 0; i < list.size() - 1; i++)
        {
            list.get(i)[1] = list.get(i)[1] + " (" + String.format("%.2f",
                Long.parseLong(list.get(i)[1]) * 100.0 / totalInstances) + ")";
            list.get(i)[2] = list.get(i)[2] + " (" + String.format("%.2f",
                Long.parseLong(list.get(i)[2]) * 100.0 / totalSize) + ")";
            
        }
        String[][] data = new String[list.size()][4];
        for (int i = 0; i < data.length; i++)
        {
            data[i] = list.get(i);
        }
        return data;
    }
}
