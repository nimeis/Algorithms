package main.compression;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {

    // >> [AF] Remove test code (especially main methods) before submitting (-1)
    public static void main(String[] args) {
        Huffman H = new Huffman("AB");
        byte[] code = H.compress("BA");
        System.out.println(H.decompress(code));
    }
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    // TreeMap chosen here just to make debugging easier
    private TreeMap<Character, String> encodingMap = new TreeMap<>();
    // Character that represents the end of a compressed transmission
    private static final char ETB_CHAR = 23;
    // >> [AF] Data structures like the character frequencies and priority queue apply ONLY
    // to the construction of the Huffman object, and therefore should not need to be saved
    // as fields -- if you needed access to these in other helper methods, they should be
    // passed as parameters. Save fields for data that should persist between method calls (-0.5)
    //Map letters to freqs
    Map<Character,Integer> freqs = new HashMap<>();
    //Priority que used to construct trie from unstructured huffman nodes
    PriorityQueue<HuffNode> unStructuredNodes = new PriorityQueue<HuffNode>();
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * 
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    public Huffman (String corpus) {
        // >> [AF] Nice helper method decomposition though, quite nice!
        countFreqs(corpus);
        createTrie();
        createEncodingMap(trieRoot, "");
        
    }

    /**
     * Counts the frequency of characters using the character
     * distributions in the given text corpus
     * 
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     * @return void
     */
    public void countFreqs(String corpus) {
            // >> [AF] Remember that helper methods should be private. Also, indent 4 spaces only in a
            // code block / method body -- this has 8 (-1)
            for (char ch : corpus.toCharArray()) {
                Integer chCount = freqs.putIfAbsent(ch, 1);
                if (chCount != null)
                {
                    freqs.put(ch, chCount + 1);
                } 
            }

            for (Map.Entry<Character,Integer> node : freqs.entrySet()) {
                unStructuredNodes.add(new HuffNode(node.getKey(), node.getValue()));
            }

            unStructuredNodes.add(new HuffNode(ETB_CHAR, 1));

    }

    /**
     * Structures the huffnodes as a tree
     * 
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     * @return void
     */
    public void createTrie() {

        while(unStructuredNodes.size() >= 2){
            HuffNode node1 = unStructuredNodes.remove();
            HuffNode node2 = unStructuredNodes.remove();

            // >> [AF] Looks like you modified the HuffNode class to have a constructor ignoring
            // the character argument: problematic!
            // >> [AF] Here's a problem: remember that nodes are prioritized by frequency first
            // but then with ties broken by their character field. What happens if you give all non-leaves
            // the same character? Review the tiebreaking criteria, which is earliest character *in a subtree*
            HuffNode combinedNode = new HuffNode(node1.count + node2.count);
            combinedNode.setChildren(node1,node2);

            unStructuredNodes.add(combinedNode);
        }

        trieRoot = unStructuredNodes.remove();
}

    /**
     * Fills the encoding map
     * 
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     * @return void
     */
    public void createEncodingMap(HuffNode root, String path) {
        if(root == null)
        {
            return;
        }
        if(root.isLeaf())
        {
            encodingMap.put(root.character, path);
        }

        createEncodingMap(root.zeroChild, path + "0");
        createEncodingMap(root.oneChild, path + "1");
}  
    
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * 
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as:
     *         (1) the bitstring containing the message itself, (2) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
        // >> [AF] The compress method can be broken down into constituent pieces to reduce the clutter
        // in a single method: (a) getting the String of compressed bits for the message and (b) converting
        // that bitstring into the byte array. (-0.5)
        message = message + ETB_CHAR;
        String bitString = "";
        for (char encodeLetter : message.toCharArray()) {
            bitString += encodingMap.get(encodeLetter);
        }
        bitString = pad(bitString);
        int numBytes = bitString.length()/8;
        byte[] bytes = new byte[numBytes];
        int b = 0;
        for (int i = 0; i < bitString.length(); i+=8) {
            String byteString = bitString.substring(i, i+8);
            bytes[b] = (byte) Integer.parseInt(byteString, 2);
            b++;
        }
        return bytes;
    }

    // >> [AF] As laborious as it is, remember that ALL methods, helpers or otherwise, should
    // have accompanying JavaDocs (-1)
    private String pad(String s){
        StringBuilder sb = new StringBuilder(s);
        while(sb.length() % 8 != 0){
            sb.append("0");
        }
        return sb.toString();
    }
    private String frontpad(String s){
        StringBuilder sb = new StringBuilder(s);
        while(sb.length() % 8 != 0){
            sb.insert(0, "0");
        }
        return sb.toString();
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * 
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as:
     *        (1) the bitstring containing the message itself, (2) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        String bitString = "";
        String decompressedString = "";
        HuffNode root = trieRoot;
        for (byte b : compressedMsg) {
            bitString += frontpad(Integer.toBinaryString(b & 0xff)); 
        }
        
        for (char charbit : bitString.toCharArray()) {
            int bit = Character.getNumericValue(charbit);
            root = root.child(bit);
            if(root.character == ETB_CHAR){
                break;
            }
            if(root.isLeaf()){
                decompressedString += root.character;
                root = trieRoot;
            }
        }

        return decompressedString;

    }

    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left (0) and right (1) child), contains
     * a character field that it represents, and a count field that holds the 
     * number of times the node's character (or those in its subtrees) appear 
     * in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode zeroChild, oneChild;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        HuffNode (int count) {
            this.count = count;
            this.character = Character.MIN_VALUE;
        }
        public HuffNode child(int index){
            if (index == 0){
                return this.zeroChild;
            } 
            else if(index == 1){
                return this.oneChild;

            } else{
                throw new IllegalArgumentException();
            } 
        }
        
        public void setChildren(HuffNode child0, HuffNode child1){
            this.zeroChild = child0;
            this.oneChild = child1;
        }
        
        public boolean isLeaf () {
            return this.zeroChild == null && this.oneChild == null;
        }
        
        public int compareTo (HuffNode other) {
            if(this.count == other.count){
                if(this.character == ETB_CHAR){
                    return -1;
                }
                return(this.character - other.character);
            }else{
                return this.count - other.count;
            }
            
        }
        
    }

}

// ===================================================
// >>> [AF] Summary
// A solid effort that has a lot to like, and I'd say
// you got the main pieces of the algorithm down, but
// just didn't have the time to test some crucial edge
// cases that could've exposed the bugs with your
// tie breaking mechanism. Likewise, remember to give
// yourself time to lint your submission for stylistic
// improvements, which are often as important as the
// functional aspects when it comes time to technical
// interviews and portfolio display. Onwards to the
// next!
// ---------------------------------------------------
// >>> [AF] Style Checklist
// [X] = Good, [~] = Mixed bag, [ ] = Needs improvement
//
// [~] Variables and helper methods named and used well
// [~] Proper and consistent indentation and spacing
// [ ] Proper JavaDocs provided for ALL methods
// [X] Logic is adequately simplified
// [X] Code repetition is kept to a minimum
// ---------------------------------------------------
// Correctness:        79.0 / 100 (-1.5 / missed test)
// Style Penalty:     - 4.0
// Late (-20%):       -16.0
// Total:              59.0 / 100
// ===================================================
