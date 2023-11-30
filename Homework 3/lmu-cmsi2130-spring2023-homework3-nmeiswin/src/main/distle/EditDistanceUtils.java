package main.distle;

import java.util.*;

public class EditDistanceUtils {
    
    /**
     * Returns the completed Edit Distance memoization structure, a 2D array
     * of ints representing the number of string manipulations required to minimally
     * turn each subproblem's string into the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return Completed Memoization structure for editDistance(s0, s1)
     */

    public static int[][] getEditDistTable (String s0, String s1) {
        int[][] table = new int[s0.length()+1][s1.length()+1];
        for (int r = 0; r <= s0.length(); r++)
        {
            for(int c = 0; c <= s1.length(); c++)
            {
                table[r][c] = reccurence(s0,s1,table,r,c);
            }
        }

        return table;
        
    }
            

    public static int reccurence(String s0, String s1, int[][] table,int r, int c){
        int bestScore = Integer.MAX_VALUE;
        if (c == 0)
        {
            return r;
        }
        if (r == 0)
        {
            return c;
        }
        if (c >= 1)
        {
            int score = table[r][c-1] + 1;
            if (score < bestScore){
                bestScore = score;
            }
        }
        if (r >= 1)
        {
            int score = table[r-1][c] + 1;
            if (score < bestScore){
                bestScore = score;
            }
        }
        if (r >=1 && c>=1)
        {
            int score = table[r-1][c-1] + ((s1.charAt(c-1)==s0.charAt(r-1)) ? 0:1);
            if (score < bestScore){
                bestScore = score;
            }
        }
        if (r >=2 && c>=2)
        {
            if (s0.charAt(r-1) == s1.charAt(c-2) && s0.charAt(r-2) == s1.charAt(c-1))
            {
                int score = table[r-2][c-2] + 1;
                if (score < bestScore){
                bestScore = score;
            }
            }
        }
        
        return bestScore;
    }
    
    /**
     * Returns one possible sequence of transformations that turns String s0
     * into s1. The list is in top-down order (i.e., starting from the largest
     * subproblem in the memoization structure) and consists of Strings representing
     * the String manipulations of:
     * <ol>
     *   <li>"R" = Replacement</li>
     *   <li>"T" = Transposition</li>
     *   <li>"I" = Insertion</li>
     *   <li>"D" = Deletion</li>
     * </ol>
     * In case of multiple minimal edit distance sequences, returns a list with
     * ties in manipulations broken by the order listed above (i.e., replacements
     * preferred over transpositions, which in turn are preferred over insertions, etc.)
     * @param s0 String transforming into other
     * @param s1 Target of transformation
     * @param table Precomputed memoization structure for edit distance between s0, s1
     * @return List that represents a top-down sequence of manipulations required to
     * turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements followed
     * by a transposition, then insertion.
     */
    public static List<String> getTransformationList (String s0, String s1, int[][] table) {
        int r = table.length-1;
        int c = table[0].length-1;
        List<String> transformations = new ArrayList<>();
        while ((r != 0) || (c != 0))
        {
            List<Object> newIndeces = backTrackReccurence(s0, s1, r,c,table);
            r = (int) newIndeces.get(0);
            c = (int) newIndeces.get(1);
            if (newIndeces.get(2) != null)
            {
                transformations.add((String) newIndeces.get(2));
            }
        }
        return transformations;
    }

    public static List<Object> backTrackReccurence(String s0, String s1, int r, int c, int[][] table)
    {
        if (c == 0)
        {
            return Arrays.asList(r-1,c,"D");
        }
        if (r == 0)
        {
            return Arrays.asList(r,c-1,"I");
        }
        //replacement
        if (r >=1 && c>=1)
        {
            int score = table[r-1][c-1] + ((s1.charAt(c-1)==s0.charAt(r-1)) ? 0:1);
            if (score == table[r][c]){
                if ((s1.charAt(c-1)==s0.charAt(r-1)))
                {
                    return Arrays.asList(r-1,c-1,null);
                }
                else{
                    return Arrays.asList(r-1,c-1,"R");
                }
                
            }
        }
        //transposition
        if (r >=2 && c>=2)
        {
            if (s0.charAt(r-1) == s1.charAt(c-2) && s0.charAt(r-2) == s1.charAt(c-1))
            {
                int score = table[r-2][c-2] + 1;
                if (score == table[r][c]){
                return Arrays.asList(r-2,c-2,"T");
            }
            }
        }
        //insertion
        if (c >= 1)
        {
            int score = table[r][c-1] + 1;
            if (score == table[r][c]){
                return Arrays.asList(r,c-1,"I");
            }
        }
        if (r >= 1)
        {
            int score = table[r-1][c] + 1;
            if (score == table[r][c]){
                return Arrays.asList(r-1,c,"D");
            }
        }
        return null;
    }
    
    /**
     * Returns the edit distance between the two given strings: an int
     * representing the number of String manipulations (Insertions, Deletions,
     * Replacements, and Transpositions) minimally required to turn one into
     * the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return The minimal number of manipulations required to turn s0 into s1
     */
    public static int editDistance (String s0, String s1) {
        if (s0.equals(s1)) { return 0; }
        return getEditDistTable(s0, s1)[s0.length()][s1.length()];
    }
    
    /**
     * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
     */
    public static List<String> getTransformationList (String s0, String s1) {
        return getTransformationList(s0, s1, getEditDistTable(s0, s1));
    }

}
