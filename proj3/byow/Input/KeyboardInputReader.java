package byow.Input;

import edu.princeton.cs.introcs.StdDraw;

public class KeyboardInputReader implements InputReader {
    @Override
    public Character readInput() {
        if (StdDraw.hasNextKeyTyped()){
            return StdDraw.nextKeyTyped();
        }
        return null;
    }
}
