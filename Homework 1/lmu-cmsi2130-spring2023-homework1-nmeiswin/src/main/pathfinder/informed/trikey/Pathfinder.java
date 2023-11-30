package main.pathfinder.informed.trikey;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first
 * tree search.
 */
public class Pathfinder {

    /**
     * Given a MazeProblem, which specifies the actions and transitions available in
     * the search, returns a solution to the problem as a sequence of actions that
     * leads from the initial state to the collection of all three key pieces.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return A List of Strings representing actions that solve the problem of the
     *         format: ["R", "R", "L", ...]
     */
    // >> [TN] Make sure to remove code you wrote for testing purposes, like this main method (-0.5)
    public static void main(String[] args) {
        
        String[] maze = {
            //012345678
            "XXXXXXXXXXXXXXXXXX",
            "X.......I.....X.2X",
            "X.............X..X",
            "XMMMMMM.......X.MX",
            "X..1.....MM...X..X",
            "X..MXX........XX.X",
            "XXXXM.XXXX..MMX..X",
            "X3..M............X",
            "XXXXXXXXXXXXXXXXXX" // 4
          };      
            MazeProblem prob = new MazeProblem(maze);
            
            List<String> solution = Pathfinder.solve(prob);
            solution.size();
            
            

    }

    // >> [TN] Missing the Javadoc comments (-0.5)
    // You also don't need to comment as frequently as you do. In line comments should only be used
    // for dense lines of code that are difficult to parse.
    public static List<String> solve(MazeProblem problem) {

        //Create Priority Queue to sort frontier
        // >> [TN] It's a bit cleaner to define a compareTo function in STN class
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<SearchTreeNode>(11, new Comparator<SearchTreeNode>() {

            //Define comparitor for priority Queue
            @Override
            public int compare(SearchTreeNode o1, SearchTreeNode o2) {
                return o1.totalCost-o2.totalCost;
            }
            
        });

        //add initial state to frontier
        frontier.add(new SearchTreeNode(problem.getInitial(), null, null, 0,0));
        
        //obtain set of goals;
        Set<MazeState> goals = problem.getKeyStates();

        //define goal set for node goal test
        SearchTreeNode.GOAL_SET = goals;

        //define graveyard
        Set<SearchTreeNode> graveyard = new HashSet<SearchTreeNode>();
        
        //initial expansion
        // >> [TN] You shouldn't need more than one new line to separate blocks of code
        SearchTreeNode currNode = frontier.remove();
        graveyard.add(currNode);


        //initial generation
        Map<String, MazeState> transitions = problem.getTransitions(currNode.state);

        //iterate through possible transistions
        for (Map.Entry<String,MazeState> posibleState : transitions.entrySet()) 
        {
            //calculate lowest future cost to key states not yet reached
            int futureCost = Integer.MAX_VALUE;
            for (MazeState goal : goals) 
            {
                // >> [TN] Uneven indentation here -- remember that each code block is indented 4 spaces and
                // all bracket indents should match their siblings' (-0.25)
                // This block of code could be a helper method
                if (!currNode.goalsHit.contains(goal))
                {
                int posibleCost = Math.abs((goal.row()-posibleState.getValue().row()))+Math.abs((goal.col()-posibleState.getValue().col()));
                futureCost = (futureCost > posibleCost)? posibleCost : futureCost;
                }


                
            }

            //Calculate path cost
            int pathCost = (currNode.pathCost + problem.getCost(posibleState.getValue()));

            //create temperary node
            SearchTreeNode prospectNode = new SearchTreeNode(posibleState.getValue(), posibleState.getKey(), currNode, pathCost,pathCost + futureCost);
            //fill node with goals hit along the path so far
            prospectNode.goalsHit.addAll(currNode.goalsHit);
            
            //check if prospect node is a goal to add it to goals hit along path
            if (futureCost == 0)
            {
                prospectNode.goalsHit.add(posibleState.getValue());
                //add to frontier if not in graveyard
                if(!graveyard.contains(prospectNode))
                {
                    frontier.add(prospectNode);
                    graveyard.add(prospectNode);
                }
                
            }
            else{
                //add to frontier if not in graveyard
                if(!graveyard.contains(prospectNode))
                {
                    frontier.add(prospectNode);
                    graveyard.add(prospectNode);
                }
                
            }
            
        }

        //Main search loop
        while(!currNode.goalComplete())
        {
            //Check if the problem is impossible
            if (frontier.isEmpty())
            {
                return null;
            }

            ////////////EXPLORATION/////////////

            //explores node if not in graveyard
            currNode = frontier.remove();
            graveyard.add(currNode);

            //goal check
            if (currNode.goalComplete())
            {
                break;
            }

            ////////////GENERATION/////////////
            
            //update transitions
            transitions = problem.getTransitions(currNode.state);

            //iterate through possible transistions
        for (Map.Entry<String,MazeState> posibleState : transitions.entrySet()) 
        {
            //calculate lowest future cost to key states not yet reached
            int futureCost = Integer.MAX_VALUE;
            // >> [TN] The fact that you have this block of code a second time shows that it should be a helper that is called in each place
            for (MazeState goal : goals) 
            {
                if (!currNode.goalsHit.contains(goal))
                {
                int posibleCost = Math.abs((goal.row()-posibleState.getValue().row()))+Math.abs((goal.col()-posibleState.getValue().col()));
                futureCost = (futureCost > posibleCost)? posibleCost : futureCost;
                }
                // >> [TN] Three newlines is more than you'll need. Stick to one and be consistent


                
            }

            //Calculate path cost
            int pathCost = (currNode.pathCost + problem.getCost(posibleState.getValue()));

            //create temperary node
            SearchTreeNode prospectNode = new SearchTreeNode(posibleState.getValue(), posibleState.getKey(), currNode, pathCost,pathCost + futureCost);
            //fill node with goals hit along the path so far
            prospectNode.goalsHit.addAll(currNode.goalsHit);
            
            //check if prospect node is a goal to add it to goals hit along path
            if (futureCost == 0)
            {
                prospectNode.goalsHit.add(posibleState.getValue());
                //add to frontier if not in graveyard
                if(!graveyard.contains(prospectNode))
                {
                    frontier.add(prospectNode);
                    graveyard.add(prospectNode);
                }
                
            }
            else{
                //add to frontier if not in graveyard
                if(!graveyard.contains(prospectNode))
                {
                    frontier.add(prospectNode);
                    graveyard.add(prospectNode);
                }
                
            }
            
        }
                //END OF SEARCH LOOP
    }


      
        
    List<String> solution = new ArrayList<String>();

        //Back trace steps and create solution array
        // >> [TN] You have a lot of code here to recover the solution that should be instead
        // in its own helper method (see CW2 solution) (-0.25)
        while(currNode.parent != null)
        {
            solution.add(0,(currNode.action));
            currNode = currNode.parent;
            
        }

        return solution; 

        
    }
    

    /**
     * SearchTreeNode private static nested class that is used in the Search
     * algorithm to construct the Search tree.
     * [!] You may do whatever you want with this class -- in fact, you'll need 
     * to add a lot for a successful and efficient solution!
     */
    private static class SearchTreeNode{

        MazeState state;
        String action;
        SearchTreeNode parent;
        int pathCost;
        int totalCost;
        
        //Goals given by problem
        private static Set<MazeState> GOAL_SET = new HashSet<MazeState>();
        //Goals collected along path
        Set<MazeState> goalsHit = new HashSet<MazeState>();

        /**
         * Constructs a new SearchTreeNode to be used in the Search Tree.
         * 
         * @param state  The MazeState (row, col) that this node represents.
         * @param action The action that *led to* this state / node.
         * @param parent Reference to parent SearchTreeNode in the Search Tree.
         * @param totalCost Cost g(n) + h(n).
         */
        SearchTreeNode(MazeState state, String action, SearchTreeNode parent,int pathCost,int totalCost) {
            this.state = state;
            this.action = action;
            this.parent = parent;
            this.pathCost = pathCost;
            this.totalCost = totalCost;
            
            
        }


        /**
         * Returns wether or not path has hit all goals
         */
        public boolean goalComplete()
        {
            return goalsHit.containsAll(GOAL_SET);
        }

        /**
         * Overidden! checks for state and goalsHit equality
         */
        @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        return other.getClass() == this.getClass()
                ? this.state.equals(((SearchTreeNode) other).state) && this.goalsHit.equals(((SearchTreeNode) other).goalsHit)
                : false;
    }

        /**
         * Overidden! Hashes node using state and goals hit
         */
        @Override
        public int hashCode()
        {
            return Objects.hash(this.state,this.goalsHit);
        }

        
    }
}

// ===================================================
// >>> [TN] Summary
// Nice job on this assignment! Code works excellently.
// Style wise, the two big things are helper methods and
// white space. Make sure that if you use new lines, that
// they are used consistently and only one is used. Any
// more is unnecessary. Repeated chunks of code and large
// chunks should be separated into helper methods for 
// readability. Remember DRY: Don't Repeat Yourself.
// ---------------------------------------------------
// >>> [TN] Style Checklist
// [X] = Good, [~] = Mixed bag, [ ] = Needs improvement
//
// [ ] Variables and helper methods named and used well
// [ ] Proper and consistent indentation and spacing
// [X] Proper JavaDocs provided for ALL methods
// [X] Logic is adequately simplified
// [X] Code repetition is kept to a minimum
// ---------------------------------------------------
// Correctness:         100 / 100 (-1.5 / missed unit test)
//   -> Refunded 2 grading tests, so will be out of 28
//      not 30
// You passed all 30 tests so extra credit keeps you at 100
// Style Penalty:       -1.5
// Total:               100 / 100
// ===================================================

