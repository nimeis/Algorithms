package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.util.*;

/**
 * AI Distle Player! Contains all logic used to automagically play the game of
 * Distle with frightening accuracy (hopefully).
 */
public class DistlePlayer {
    
    int numGuessesLeft = 0;
    String lastGuess = null;
    int numGuesses = 0;
    Set<String> wordBank;
    int wordLength = -1;
    ArrayList<Set<Character>> letterGraveyards = new ArrayList<>();

    public static void main(String[] args) {
        List<String> wordTransforms = getTransformationList("dei", "leggy", getEditDistTable("slily", "leggy"));
        System.out.println(wordTransforms.toString());
    }
    
    /**
     * Constructs a new DistlePlayer.
     * [!] You MAY NOT change this signature, meaning it may not accept any arguments.
     * Still, you can use this constructor to initialize any fields that need to be,
     * though you may prefer to do this in the {@link #startNewGame(Set<String> dictionary, int maxGuesses)}
     * method.
     */
    public DistlePlayer () {
        // [!] TODO: Any initialization of fields you want here (can also leave empty)
    }
    
    /**
     * Called at the start of every new game of Distle, and parameterized by the
     * dictionary composing all possible words that can be used as guesses / one of
     * which is the correct.
     * 
     * @param dictionary The dictionary from which the correct answer and guesses
     * can be drawn.
     * @param maxGuesses The max number of guesses available to the player.
     */
    public void startNewGame (Set<String> dictionary, int maxGuesses) {
        wordBank = dictionary;
        numGuessesLeft = maxGuesses;
        numGuesses = maxGuesses;
        wordLength = -1;
        letterGraveyards = new ArrayList<>();
    }
    
    /**
     * Requests a new guess to be made in the current game of Distle. Uses the
     * DistlePlayer's fields to arrive at this decision.
     * 
     * @return The next guess from this DistlePlayer.
     */
    public String makeGuess() {
        int furthestDist = 0;
        String guess;
        if (lastGuess != null)
        {
            String furthestString = lastGuess;
            for (String s : wordBank) {
                if (editDistance(s, furthestString) > furthestDist)
                {
                    furthestString = s;
                }
            }
                guess = furthestString;
        }else{
            guess = wordBank.stream().skip(new Random().nextInt(wordBank.size())).findFirst().orElse(null);
        }
            wordBank.remove(guess);
            return guess;
    }
    
    /**
     * Called by the DistleGame after the DistlePlayer has made an incorrect guess. The
     * feedback furnished is as follows:
     * <ul>
     *   <li>guess, the player's incorrect guess (repeated here for convenience)</li>
     *   <li>editDistance, the numerical edit distance between the guess and secret word</li>
     *   <li>transforms, a list of top-down transforms needed to turn the guess into the secret word</li>
     * </ul>
     * [!] This method should be used by the DistlePlayer to update its fields and plan for
     * the next guess to be made.
     * 
     * @param guess The last, incorrect, guess made by the DistlePlayer
     * @param editDistance Numerical distance between the guess and the secret word
     * @param transforms List of top-down transforms needed to turn the guess into the secret word
     */
    public void getFeedback (String guess, int editDistance, List<String> transforms) {
        lastGuess = guess;
        //Decyper word lengthh
        if(wordLength == -1) // -1 -> not initialized
        {
            wordLength = guess.length();
            for (String letter : transforms) {
                if (letter == "I")
                {
                    wordLength++;
                }
                if (letter == "D")
                {
                    wordLength--;
                }
            }
            for (int i = 0; i < wordLength;i++)
            {
                letterGraveyards.add(new HashSet<>());
            }
        }
        Set<String> transformsUsed = new HashSet<>(transforms);
        if (transforms.size() == guess.length() && transformsUsed.size() == 1 && transformsUsed.contains("R"))
        {
            for (int i = 0; i < wordLength;i++)
            {
                letterGraveyards.get(i).add(guess.charAt(i));
            }
        }
        Set<String> newWordBank = new HashSet<>();
        for (String word : wordBank) {
            List<String> wordTransforms = getTransformationList(guess, word, getEditDistTable(guess, word));
            if (wordTransforms.equals(transforms) && word.length() == wordLength)
            {
                Boolean educatedGuess = true;
                for (int i = 0; i < wordLength;i++)
                {
                    if(letterGraveyards.get(i).contains(word.charAt(i)))
                    {
                    educatedGuess = false;
                    }
                } 
                if(educatedGuess)
                {
                    newWordBank.add(word);
                }
            }
        }
        wordBank = newWordBank;
    }  
}
