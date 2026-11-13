package byow.Input;

import edu.princeton.cs.introcs.StdDraw;

public class StringInputReader implements InputReader {
    private final char[] input;
    private int currentIndex;
    public StringInputReader(String inputString){
        if (inputString==null){
            input=new char[0];
            return;
        }
       input=inputString.toCharArray();
       currentIndex=0;
    }
    @Override
    public Character readInput() {
        if (currentIndex<input.length){
            Character in=input[currentIndex];
            currentIndex++;
            return in;
        }
        return null;
    }
}
