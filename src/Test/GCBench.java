package Test;

import javax.swing.JButton;
import javax.swing.JFrame;

class Node
{
    Node left, right;
    
    int i, j;
    
    Node(Node l, Node r)
    {
        left = l;
        right = r;
    }
    
    Node()
    {
    }
}

public class GCBench
{
    
    public static final int kStretchTreeDepth = 18; // about 16Mb
    
    public static final int kLongLivedTreeDepth = 16; // about 4Mb
    
    public static final int kArraySize = 500000; // about 4Mb
    
    public static final int kMinTreeDepth = 4;
    
    public static final int kMaxTreeDepth = 16;
    
    // Nodes used by a tree of a given size
    static int TreeSize(int i)
    {
        return ((1 << (i + 1)) - 1);
    }
    
    // Number of iterations to use for a given tree depth
    static int NumIters(int i)
    {
        return 2 * TreeSize(kStretchTreeDepth) / TreeSize(i);
    }
    
    // Build tree top down, assigning to older objects.
    static void Populate(int iDepth, Node thisNode)
    {
        if (iDepth <= 0)
        {
            return;
        }
        else
        {
            iDepth--;
            thisNode.left = new Node();
            thisNode.right = new Node();
            Populate(iDepth, thisNode.left);
            Populate(iDepth, thisNode.right);
        }
    }
    
    // Build tree bottom-up
    static Node MakeTree(int iDepth)
    {
        if (iDepth <= 0)
        {
            return new Node();
        }
        else
        {
            return new Node(MakeTree(iDepth - 1), MakeTree(iDepth - 1));
        }
    }
    
    static void PrintDiagnostics()
    {
        long lFreeMemory = Runtime.getRuntime().freeMemory();
        long lTotalMemory = Runtime.getRuntime().totalMemory();
        
        System.out.print(" Total memory available=" + lTotalMemory + " bytes");
        System.out.println("  Free memory=" + lFreeMemory + " bytes");
    }
    
    static void TimeConstruction(int depth)
    {
        Node root;
        long tStart, tFinish;
        int iNumIters = NumIters(depth);
        Node tempTree;
        
        System.out.println("Creating " + iNumIters + " trees of depth " + depth);
        tStart = System.currentTimeMillis();
        for (int i = 0; i < iNumIters; ++i)
        {
            tempTree = new Node();
            Populate(depth, tempTree);
            tempTree = null;
        }
        tFinish = System.currentTimeMillis();
        System.out
            .println("\tTop down construction took " + (tFinish - tStart) + "msecs");
        tStart = System.currentTimeMillis();
        for (int i = 0; i < iNumIters; ++i)
        {
            tempTree = MakeTree(depth);
            tempTree = null;
        }
        tFinish = System.currentTimeMillis();
        System.out
            .println("\tBottom up construction took " + (tFinish - tStart) + "msecs");
        
    }
    
    public static void test()
    {
        Node root;
        Node longLivedTree;
        Node tempTree;
        long tStart, tFinish;
        long tElapsed;
        
        System.out.println("Garbage Collector Test");
        System.out.println(
            " Stretching memory with a binary tree of depth " + kStretchTreeDepth);
        PrintDiagnostics();
        tStart = System.currentTimeMillis();
        
        // Stretch the memory space quickly
        tempTree = MakeTree(kStretchTreeDepth);
        tempTree = null;
        
        // Create a long lived object
        System.out.println(
            " Creating a long-lived binary tree of depth " + kLongLivedTreeDepth);
        longLivedTree = new Node();
        Populate(kLongLivedTreeDepth, longLivedTree);
        
        // Create long-lived array, filling half of it
        System.out.println(" Creating a long-lived array of " + kArraySize + " doubles");
        double array[] = new double[kArraySize];
        for (int i = 0; i < kArraySize / 2; ++i)
        {
            array[i] = 1.0 / i;
        }
        PrintDiagnostics();
        
        for (int d = kMinTreeDepth; d <= kMaxTreeDepth; d += 2)
        {
            TimeConstruction(d);
        }
        
        if (longLivedTree == null || array[1000] != 1.0 / 1000)
            System.out.println("Failed");
        // fake reference to LongLivedTree
        // and array
        // to keep them from being optimized away
        
        tFinish = System.currentTimeMillis();
        tElapsed = tFinish - tStart;
        PrintDiagnostics();
        System.out.println("Completed in " + tElapsed + "ms.");
    }

    public static void main(String[] args)
    {
        new Frame();
    }
}

class Frame extends JFrame
{
    public Frame()
    {
        JButton button = new JButton("test");
        button.addActionListener(e -> GCBench.test());
        this.add(button);
        this.setSize(300, 100);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}