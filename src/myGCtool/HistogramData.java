package myGCtool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to get and wrap the heap histogram data.
 */
public class HistogramData
{
    
    /**
     * 
     * Get the heap histogram data of the process of the specified pid
     * 
     * @param count how many rows of data to get
     * @param pid the pid of the specified process
     * @return a list of heap histogram data arrays which is [line num,instance count,size,class]
     *         and the last array is ["all objects in heap",total instances,total size,""]
     */
    private List<String[]> addData(int count, String pid)
    {
        // the list to store the data array
        List<String[]> heapDataList = new ArrayList<>();
        Process exec = null;
        BufferedReader reader = null;
        try
        {
            try
            {
                // execute "jmap -histo <pid>" to get heap histogram data
                exec = Runtime.getRuntime().exec("jmap -histo " + pid);
                // read data from the result
                reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                if (reader.readLine() == null)// read first line,useless(title)
                    return null;// there is no data return null (process has been terminated)
                reader.readLine();// read the second line,useless("---")
                // read useful data
                for (int i = 0; i < count; i++)
                {
                    // data array to store data line
                    String[] data = new String[4];
                    String line = reader.readLine();
                    // split with double space,avoid missing data
                    String[] lineInfo = line.split("  ");
                    int index = 0;// index of data array
                    // Get data item
                    for (int j = 0; j < lineInfo.length; j++)
                    {
                        // avoid space
                        if (!lineInfo[j].equals(""))
                        {
                            // Eliminate leading and trailing spaces and add to the data array
                            data[index] = lineInfo[j].trim();
                            index++;
                        }
                    }
                    // add data array to data list
                    heapDataList.add(data);
                }
                // the last line of the result
                String lastLine = null;
                String temp = null;// a temp to store data line
                // get the last line of the data
                while ((temp = reader.readLine()) != null)
                {
                    // read data line by line
                    // finally temp is null and lastLine is the last line of the data
                    lastLine = temp;
                }
                // split with double space,avoid missing data
                String[] strs = lastLine.split("  ");
                String[] totalData = new String[4];// used to store last line data
                int index = 0;// index of totalData
                for (int i = 0; i < strs.length; i++)// there should be 3 data items
                {
                    // avoid space
                    if (!strs[i].equals(""))
                    {
                        // Eliminate leading and trailing spaces and add to the data array
                        totalData[index] = strs[i].trim();
                        index++;
                    }
                }
                
                totalData[0] = "all objects in heap";// set first data item
                totalData[3] = "";// set null to ""
                heapDataList.add(totalData);// add data array to data list
            }
            finally
            {
                // close data source stream
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
    
    /**
     * Format data to display in JTable
     * 
     * @param pid The id of the process to get the heap histogram data
     * @return formatted 2D array that can be shown in JTable
     */
    public String[][] rowData(String pid)
    {
        // get top 10 data
        List<String[]> list = addData(10, pid);
        if (list == null)
            return null;// return null if there is no data
        String[] total = list.get(list.size() - 1);// get last line data array(the last item of the data list)
        Long totalInstances = Long.parseLong(total[1]);// the total instances
        Long totalSize = Long.parseLong(total[2]);// the total size
        // add percentage data to the data list
        for (int i = 0; i < list.size() - 1; i++)
        {
            // format: current instance (percentage),current size(percentage)
            // Instance percent = current instance count / total instance(Keep two decimals)
            list.get(i)[1] = list.get(i)[1] + " (" + String.format("%.2f",
                Long.parseLong(list.get(i)[1]) * 100.0 / totalInstances) + ")";
            // size percent = current size count / total instance(Keep two decimals)
            list.get(i)[2] = list.get(i)[2] + " (" + String.format("%.2f",
                Long.parseLong(list.get(i)[2]) * 100.0 / totalSize) + ")";
            
        }
        String[][] data = new String[list.size()][4];// used to store the formatted data
        
        for (int i = 0; i < data.length; i++)
        {
            // get array from data list and add the array to the data 2D array
            data[i] = list.get(i);
        }
        return data;
    }
}
