/******************************************************************************
 *  Name:    Greg Umali
 * 
 *  Description:  Simulates a board of tiles in which the goal is to get all
 *                the tiles into ascending order (row-major). 0 is used to
 *                represent the blank tile.
 * 
 ******************************************************************************/
import edu.princeton.cs.algs4.Queue;

public class Board {
    
    // represents the board with an array of ints from 1 to N^2-1
    // in the correct board, each index (i) contains the value (i + 1)
    private final int[] iaBoard;
    // board dimensions
    private final int iN;
    // manhattan distance of the board, cached
    private final int iManhattan;
    
    // construct a board from an N-by-N array of tiles
    // (where tiles[i][j] = tile at row i, column j)
    public Board(int[][] tiles) {
        int iRow = tiles.length;
        int iCol = tiles[0].length;
        
        // assumes that row == col, arbitrarily uses row to assign a value to iN
        iN = iRow;
        // creates a new 1D array of size N^2
        iaBoard = new int[iRow * iCol];
        
        // iterate through 2D array, put into 1D representation by row
        // therefore, the 2D array [ A, B ] is represented as [A, B, C, D] in 1D
        //                         [ C, D ]
        for (int i = 0; i < iRow; i++) {
            for (int j = 0; j < iCol; j++) {
                iaBoard[conv2Dto1D(i, j)] = tiles[i][j];
            }
        }
        // only want to call this function once
        iManhattan = manhattan();
    }
    
    // helper method to convert a 2D coordinate to its place in a 1D array
    private int conv2Dto1D(int iRow, int iCol) {
        return (iN * iRow) + iCol;
    }
                                           
    // return tile at row i, column j (or 0 if blank)
    public int tileAt(int i, int j) {
        // condition checks if i and j are in range
        if (i < 0 || i > (iN - 1) || j < 0 || j > (iN - 1)) {
            throw new IndexOutOfBoundsException();
        }
        return iaBoard[conv2Dto1D(i, j)];
    }
    
    // board size N
    public int size() {
        return iN;
    }
    
    // number of tiles out of place
    public int hamming() {
        // in the correct board, each index (i) contains the value (i + 1)
        // linear scan through the array, counts how many values
        // do not satisfy this requirement
        int iNumMisplaced = 0;
        // only goes up to length-1 because the 0 tile shouldn't be counted
        for (int i = 0; i < iaBoard.length-1; i++) {
            if (iaBoard[i] != (i + 1)) iNumMisplaced++;
        }
        
        return iNumMisplaced;
    }
    
    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        // tracks our return value
        int iManhattanSum = 0;
        // intermediate variable for calculating the sum
        int iSumAddend;
        
        // current row and column in the board
        int iRow;
        int iCol;
        
        // goal row and column in the board
        int iGoalRow;
        int iGoalCol;
        
        for (int i = 0; i < iaBoard.length; i++) {
            // blank tile is not included in calculating the sum
            if (iaBoard[i] != 0) {
                // set the values of its current position based on index
                iRow = i / iN;
                iCol = i % iN;
                // set the values of goal position based on the value
                iGoalRow = (iaBoard[i] - 1) / iN;
                iGoalCol = (iaBoard[i] - 1) % iN;
                
                // calculate the Manhattan value based on the row and col values
                iSumAddend = (iRow - iGoalRow);
                // adjust if negative (would be easy to use Math.abs, but
                // not allowed in this assignment
                if (iSumAddend < 0) iSumAddend *= -1;
                iManhattanSum += iSumAddend;
                
                // rinse and repeat
                iSumAddend = (iCol - iGoalCol);
                if (iSumAddend < 0) iSumAddend *= -1;
                iManhattanSum += iSumAddend;
            }
        }
        return iManhattanSum;
    }
    
    // is this board the goal board?
    public boolean isGoal() {
        return (iManhattan == 0);
    }
    
    // is this board solvable?
    public boolean isSolvable() {
        int iInversions = 0;
        int iBlankRow = 0;
        
        // Iterates through each element, compares it to every element 
        // after it. If a smaller one found in scan, it is an inversion.
        for (int i = 0; i < iaBoard.length; i++) {
            for (int j = i + 1; j < iaBoard.length; j++) {
                // makes sure it doesn't count the blank tile
                if (iaBoard[j] != 0 && iaBoard[i] > iaBoard[j]) iInversions++;
            }
            // checks for the blank tile, while we're at it
            if (iaBoard[i] == 0) {
                iBlankRow = (i / iN);
            }
        }
        
        // if an odd-sized board, not solvable if number of inversions is odd
        if (iN % 2 == 1) {
            if (iInversions % 2 != 0) return false;
        }
        // if an even-sized board, not solvable if the sum of number of 
        // inversions and the row of the blank square is an even number
        else {
            if ((iInversions + iBlankRow) % 2 == 0) return false;
        }
        // tests failed, must be solvable
        return true;
    }
    
    // does this board equal y?
    public boolean equals(Object y) {
        // preliminary checks
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        
        // cast the generic object into a Board object
        Board oCompBoard = (Board) y;
        // stores row and col representations of the current index
        int iRow;
        int iCol;
        
        // makes sure the boards are the same size; if not, return false
        if (oCompBoard.size() != iN) return false;
        
        // goes through each index, compares element
        // if a difference is found, returns false
        for (int i = 0; i < iaBoard.length; i++) {
            iRow = i / iN;
            iCol = i % iN;
            if (iaBoard[i] != oCompBoard.tileAt(iRow, iCol)) return false;
        }
        // went through the whole loop without problems, return true
        return true;
    }
    
    // all neighboring boards
    public Iterable<Board> neighbors() {
        // will be used to store the boards that are neighbors
        Queue<Board> qNeighbors = new Queue<Board>();
        
        // needed to make a new Board object
        int[][] iaTiles = new int[iN][iN];
        
        // location of the blank tile
        // if these are still -1 after processing, there is no blank tile
        int iBlankRow = -1;
        int iBlankCol = -1;
        
        // tile idx adjacent north to blank; -1 if out of range
        int iNorthIdx = -1;
        // tile idx adjacent south to blank; -1 if out of range
        int iSouthIdx = -1;
        // tile idx adjacent west to blank; -1 if out of range
        int iWestIdx = -1;
        // tile idx adjacent east to blank; -1 if out of range
        int iEastIdx = -1;
        
        // used to store the row and col of the item of interest when
        // constructing our 2D int arrays
        int iRow;
        int iCol;
        
        // used to track the idx of elements in our 1D array when making a 2D
        int iIdx = 0;
        
        // used as the new Board object to be added to the Queue
        Board oBoard;
        
        // locates the blank tile
        for (int i = 0; i < iaBoard.length; i++) {
            // checks for the blank tile
            if (iaBoard[i] == 0) {
                iBlankRow = i / iN;
                iBlankCol = i % iN;
                break;
            }
        }
        
        // determines which values are adjacent to the blank tile
        if (iBlankRow - 1 >= 0) iNorthIdx = conv2Dto1D(iBlankRow - 1, iBlankCol);
        if (iBlankRow + 1 < iN) iSouthIdx = conv2Dto1D(iBlankRow + 1, iBlankCol);
        if (iBlankCol - 1 >= 0) iWestIdx = conv2Dto1D(iBlankRow, iBlankCol - 1);
        if (iBlankCol + 1 < iN) iEastIdx = conv2Dto1D(iBlankRow, iBlankCol + 1);
        
        // make a new int[][] with the current board
        // assumes iaBoard.length is N^2, and iaTiles is NxN
        // adds elements of 1D array into 2D array
        for (int i = 0; i < iN; i++) {
            for (int j = 0; j < iN; j++) {
                iaTiles[i][j] = iaBoard[iIdx++];
            }
        }
        
        // make Boards with blank tile switched with each acceptable adjacent,
        // (if the idx is not -1) and add each Board object to the Queue.
        // Later code will check whether each Board added is equal to the
        // search node's predecessor. If so, it will remove it (no repeats).
        if (iNorthIdx >= 0) {
            // switch blank tile and north tile
            switch2D(iaTiles, iBlankRow, iBlankCol,
                    (iNorthIdx / iN), (iNorthIdx % iN));

            oBoard = new Board(iaTiles);
            qNeighbors.enqueue(oBoard);
            
            // switch them back to restore old board
            switch2D(iaTiles, iBlankRow, iBlankCol, 
                     (iNorthIdx / iN), (iNorthIdx % iN));
        }
        if (iSouthIdx >= 0) {
            // switch blank tile and north tile
            switch2D(iaTiles, iBlankRow, iBlankCol, 
                     (iSouthIdx / iN), (iSouthIdx % iN));
            
            oBoard = new Board(iaTiles);
            qNeighbors.enqueue(oBoard);
            
            // switch them back to restore old board
            switch2D(iaTiles, iBlankRow, iBlankCol, 
                     (iSouthIdx / iN), (iSouthIdx % iN));
        }
        if (iEastIdx >= 0) {
            // switch blank tile and north tile
            switch2D(iaTiles, iBlankRow, iBlankCol, 
                     (iEastIdx / iN), (iEastIdx % iN));
            
            oBoard = new Board(iaTiles);
            qNeighbors.enqueue(oBoard);
            
            // switch them back to restore old board
            switch2D(iaTiles, iBlankRow, iBlankCol, 
                     (iEastIdx / iN), (iEastIdx % iN));
        }
        if (iWestIdx >= 0) {
            // switch blank tile and north tile
            switch2D(iaTiles, iBlankRow, iBlankCol, 
                     (iWestIdx / iN), (iWestIdx % iN));
            
            oBoard = new Board(iaTiles);
            qNeighbors.enqueue(oBoard);
            
            // switch them back to restore old board
            switch2D(iaTiles, iBlankRow, iBlankCol, 
                     (iWestIdx / iN), (iWestIdx % iN));
        }
        
        return qNeighbors;
    }
    
    // helper method, switches two elements in a 2D int array
    private void switch2D(int[][] a, int row1, int col1, int row2, int col2) {
        int temp = a[row1][col1];
        a[row1][col1] = a[row2][col2];
        a[row2][col2] = temp;
    }
    
    // string representation of this board (in output format specified)
    // (implementation used from the website)
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(iN + "\n");
        for (int i = 0; i < iN; i++) {
            for (int j = 0; j < iN; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        return s.toString();
    }
    
    // unit testing (required)
    public static void main(String[] args) {
        Board oBoard;
        
        int[][] iaSolved = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        Board oSolved = new Board(iaSolved);
        
        System.out.println("This is the solved board, size " + oSolved.size());
        System.out.println(oSolved);
        System.out.println("Solvable: " + oSolved.isSolvable());
        System.out.println("Is this board solved?: " + oSolved.isGoal());
        
        System.out.println();
        
        int[][] iaMixed = {{0, 1, 2}, {5, 6, 3}, {4, 7, 8}};
        Board oMixed = new Board(iaMixed);
        
        System.out.println("This is a solvable mixed board, size " + oMixed.size());
        System.out.println(oMixed);
        System.out.println("Solvable: " + oMixed.isSolvable());
        System.out.println("Is this board solved?: " + oMixed.isGoal());
        
        System.out.println("Testing the tileAt() function");
        System.out.println("Tile at (0, 0): " + oMixed.tileAt(0, 0));
        System.out.println("Tile at (N-1, N-1): " + oMixed.tileAt(2, 2));
        System.out.println("Middle Tile: " + oMixed.tileAt(1, 1));
        
        System.out.println("Hamming function: " + oMixed.hamming());
        System.out.println("Manhattan function: " + oMixed.iManhattan);
        
        System.out.println();
        
        int[][] iaMixed2 = {{1, 2, 3}, {4, 5, 6}, {8, 7, 0}};
        Board oMixed2 = new Board(iaMixed2);
        
        System.out.println("This is a unsolvable board, size " + oMixed2.size());
        System.out.println(oMixed2);
        System.out.println("Solvable: " + oMixed2.isSolvable());
        System.out.println("Is this board solved?: " + oMixed2.isGoal());
        
        System.out.println("Hamming function: " + oMixed2.hamming());
        System.out.println("Manhattan function: " + oMixed2.iManhattan);
        
        System.out.println("Neighbors of this board");
        for (Board b : oMixed2.neighbors()) System.out.println(b);
        
        System.out.println("Testing the equals function");
        System.out.println("Comparing our board with");
        System.out.println(oSolved);
        
        System.out.println("Our board is the same as this board: " + 
                           oMixed2.equals(oSolved));
        
        
        System.out.println();
        
        int[][] iaMixed3 = {{6, 5, 11, 4}, {10, 13, 2, 1}, 
                            {9, 15, 7, 3}, {14, 12, 0, 8}};
        Board oMixed3 = new Board(iaMixed3);
        
        System.out.println("This is a solvable mixed board, size " + 
                           oMixed3.size());
        System.out.println(oMixed3);
        System.out.println("Solvable: " + oMixed3.isSolvable());
        System.out.println("Is this board solved?: " + oMixed3.isGoal());
        
        System.out.println("Neighbors of this board");
        for (Board b : oMixed3.neighbors()) System.out.println(b);
        
        System.out.println("Hamming function: " + oMixed3.hamming());
        System.out.println("Manhattan function: " + oMixed3.iManhattan);   
    }
}