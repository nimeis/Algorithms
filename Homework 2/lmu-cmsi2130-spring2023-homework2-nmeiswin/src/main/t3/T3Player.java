package main.t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {
    
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of the
     * game of Tic-Tac-Total. Note: In the event that multiple moves have
     * equivalently maximal minimax scores, ties are broken by move col, then row,
     * then move number in ascending order (see spec and unit tests for more info).
     * The agent will also always take an immediately winning move over a delayed
     * one (e.g., 2 moves in the future).
     * 
     * @param state
     *            The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
    public T3Action choose (T3State state) {
        
        //keep track of wether node is max or min
        boolean max = true;
        //Initialize alpha beta values
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        //generate possible acrions
        Map<T3Action,T3State> possibleActions = state.getTransitions();
        //set up mechanisms to track best action
        int numActions = possibleActions.size();
        int[] actionScores = new int[numActions];
        int actionIndex = 0;
        ArrayList<T3Action> actions = new ArrayList<>();
        //explore every child
        for (Map.Entry<T3Action,T3State> action: possibleActions.entrySet() ) {
            //take any imedietly winning move
            if(action.getValue().isWin())
            {
                return action.getKey();
            }
            //add actions and scores to arrays to decode best move
            actionScores[actionIndex] = explore(alpha, beta, action.getValue(),!max);
            
            actions.add(action.getKey());
            actionIndex++;
        }
        
        //search for best action
        int bestMoveIndex = 0;
        for (int i = 0; i < numActions - 1;i++)
        {
            if(actionScores[i+1] > actionScores[bestMoveIndex])
            {
                bestMoveIndex = i+1;
            }
        } 
        return actions.get(bestMoveIndex);
    }
    
    private int explore(int alpha, int beta, T3State state, boolean max)
    {
        
        
        //Check if node is terminal state and return utility
        if(state.isWin())
        {
            if(max)
            {
                return -1;
            }
            else{
                return 1;
            }
        }
        if(state.isTie())
        {
            return 0;
        }

        //generate possible moves
        Map<T3Action,T3State> possibleActions = state.getTransitions();
        if (max)
        {
            int u = Integer.MIN_VALUE;
            for (Map.Entry<T3Action,T3State> action: possibleActions.entrySet() ) {
                //update utility with best move
                u = Math.max(u,explore(alpha, beta, action.getValue(),!max));
                //update alpha if we find a higher utility
                alpha = Math.max(alpha,u);
                //pruning
                if(beta <= alpha)
                {
                    break;
                }
            }
            return u;
        }
        else{
            int u = Integer.MAX_VALUE;
            for (Map.Entry<T3Action,T3State> action: possibleActions.entrySet() ) {
                //update utility with worst move
                u = Math.min(u,explore(alpha, beta, action.getValue(),!max));
                //update beta if we find a lower utility
                beta = Math.min(beta,u);
                //pruning
                if(beta <= alpha)
                {
                    break;
                }
            }
            return u;
        }
        
    
    }
    
}

