package myGCtool;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class MyTable extends JTable
{
    public MyTable(TableModel tm)
    {
        super(tm);
    }
    
    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
    
}
