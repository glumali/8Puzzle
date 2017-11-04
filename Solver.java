/******************************************************************************
 *  Name:    Greg Umali
 * 
 *  Description:  Solves the Board passed in as an argument using an A*
 *                algorithm, and can return the path taken to arrive at the
 *                solution and the number of moves required to do so.
 * 
 ******************************************************************************/
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

public class Solver {
    // constructed from the Solver by placing search nodes on the stack 
    private Stack<Board> oSolution = new Stack<Board>();
    
    // number of moves to the solution
    private int iSolutionLength;
    
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new NullPointerException();
        
        if (!initial.isSolvable()) throw new IllegalArgumentException();
        
        // will store all search nodes
        MinPQ<SearchNode> pqNodeTree = new MinPQ<SearchNode>();
        
        SearchNode oMove = new SearchNode(initial, 0, null);
        
        // enqueue this move to the MinPQ
        pqNodeTree.insert(oMove);

        int count = 0;
        while (!oMove.oBoard.isGoal()) {   
            // reassign oMove based on priority
            oMove = pqNodeTree.delMin();
            
            // generate its neighbors, enqueue them as long as they
            // aren't the same as the previous Board
            for (Board b : oMove.oBoard.neighbors()) {
                if ((oMove.oPrev == null) || !b.equals(oMove.oPrev.oBoard)) {
                    pqNodeTree.insert(new SearchNode(b, oMove.iMoves + 1, 
                                                     oMove));
                }
            }
        }
        // at this point, oMove contains the goal board.
        // number of moves taken is stored in an instance variable
        iSolutionLength = oMove.iMoves;
        // now the path of SearchNodes is traced backwards
        // to construct the solution path
        oSolution.push(oMove.oBoard);
        while (oMove.oPrev != null) {
            oSolution.push(oMove.oPrev.oBoard);
            oMove = oMove.oPrev;
        }
    }
    
    // min number of moves to solve initial board
    public int moves() {
        return iSolutionLength;
    }
    
    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return oSolution;
    }
    
    // nested class to represent a search node
    private static class SearchNode implements Comparable<SearchNode> {
        // points to prev SearchNode (null if at the beginning)
        private final SearchNode oPrev;
        
        // current board of the SearchNode
        private final Board oBoard;
       
        // number of moves to reach this SearchNode
        private final int iMoves;
        
        // priority of the SearchNode based on the Hamming function
        private final int iPriority;
        
        private SearchNode(Board board, int moves, SearchNode prev) {
            // initialize values of instance variables
            oBoard = board;
            iMoves = moves;
            oPrev = prev;
            iPriority = iMoves + oBoard.hamming();
        }
        
        public int compareTo(SearchNode other) {
            return (this.iPriority - other.iPriority);
        }
    }
    
    // unit testing 
    public static void main(String[] args) {
        int[][] iaMixed3 = {{1, 2, 3}, {0, 7, 6}, {5, 4, 8}};
        Board oMixed = new Board(iaMixed3);
        
        System.out.println("This is a solvable mixed board, size " + oMixed.size());
        System.out.println(oMixed);
        
        System.out.println();
        
        System.out.println("Best solution to this board:");
        Solver oSolver = new Solver(oMixed);
        int iStep = 0;
        for (Board b : oSolver.solution()) {
            System.out.println("Step " + iStep++ + ":\n" + b);
        }
        
        System.out.println("This solution took " + oSolver.moves() + " moves.");
        
        System.out.println();
        
        // testing for a board of size 4
        
        int[][] iaMixed4 = {{6, 5, 11, 4}, {10, 13, 2, 1}, 
                            {9, 15, 7, 3}, {14, 12, 8, 0}};
        oMixed = new Board(iaMixed4);
        
        System.out.println("This is a solvable mixed board, size " + 
                           oMixed.size());
        System.out.println(oMixed);
        
        System.out.println();
        
        System.out.println("Best solution to this board:");
        Solver oSolver2 = new Solver(oMixed);
        iStep = 0;
        for (Board b : oSolver2.solution()) {
            System.out.println("Step " + iStep++ + ":\n" + b);
        }
        
        System.out.println();
        
        System.out.println("This solution took " + oSolver2.moves() + " moves.");
    }
}