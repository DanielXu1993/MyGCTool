package myGCtool;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * Subclasses of JTable in order to prevent data cells from being edited.
 */
public class MyTable extends JTable
{
    /**
     * Constructor
     * use TableModel to construct instance
     */
    public MyTable(TableModel tm)
    {
        super(tm);
    }
    
    /**
     * override isCellEditable(int row, int column) to avoid data cells being edited
     */
    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
    
}
