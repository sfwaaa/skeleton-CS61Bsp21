package byow.Input;

//only responsible for fetching input...
public interface InputReader {
    /**
     * Fetch input,null if no input
     * @return the input represented by char,do not directly manipulate it
     */
    public Character readInput();
}
