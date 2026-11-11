package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
       // if (args.length < 1) {
       //     System.out.println("Please enter a seed");
       //     return;
       // }

       //long seed = Long.parseLong(args[0]);
        long seed =18434323;
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
       // game.playerTurn=true;
       // game.round=3;
        //game.drawFrame("dfa");
        //game.drawFrame("akbaj");
        //StdDraw.text(10,10,"Hello");
        //StdDraw.show();
        //game.flashSequence("fjakfdao");
        //game.solicitNCharsInput(5);
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */ //note that the comment seems wrong. The bottom left is (0,0) the top right is (width,height)
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        StdDraw.setPenColor(StdDraw.WHITE);
        //TODO: Initialize random number generator
       this.rand=new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < n; i++) {
           builder.append(
                   CHARACTERS[RandomUtils.uniform(rand,0,CHARACTERS.length)]);
        }
        return builder.toString();
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);

        //display center string
        StdDraw.text((double)width/2,(double)height/2,s);

        if (gameOver){
            StdDraw.show();
            return;
        }

        //display relevant info if not game over
        int topY=height-1;
        String roundString="Round:"+ round;
        StdDraw.text((double)roundString.length()/2,topY,roundString);

        String prompt="Watch!";
        if (playerTurn){
            prompt="Type!";
        }
        StdDraw.text((double)width/2,topY,prompt);

        String encourage=ENCOURAGEMENT[RandomUtils.uniform(rand,ENCOURAGEMENT.length)];
        StdDraw.text(width- (double) encourage.length() /2,topY,encourage);

        StdDraw.line(0,topY-1,width,topY-1);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        //1s = 1000ms
        long blankInterval=500;//0.5 sec
        long visibleInterval=1000;//1 sec

        long currentTimeMs;
        char[] chars=letters.toCharArray();
        for (int i = 0; i < chars.length; i++) {
           currentTimeMs=System.currentTimeMillis();
           drawFrame(Character.toString(chars[i]));
            //IT'S QUITE WEIRD...
            while ((System.currentTimeMillis()-currentTimeMs)<visibleInterval){
                //just pending. I'm sure there's some better ways to do this
            }
            //EVEN MORE WEIRD...
            drawFrame("");
            currentTimeMs=System.currentTimeMillis();
            while ((System.currentTimeMillis()-currentTimeMs)<blankInterval){
                //just pending. I'm sure there's some better ways to do this
            }
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < n; i++) {
           //RATHER WEIRD!!!
            while (!StdDraw.hasNextKeyTyped()){
                //pending...
            }
            builder.append(StdDraw.nextKeyTyped());
            drawFrame(builder.toString());
        }
        return builder.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        this.gameOver=false;
        this.round=1;

        String displayString=null;
        String inputString="";
        long visibleInterval=2000;//1 sec
        long currentTimeMs=0;
        //TODO: Establish Engine loop
        while (!gameOver){
            this.playerTurn=false;
            currentTimeMs=System.currentTimeMillis();
            drawFrame("Round:"+round);
            while ((System.currentTimeMillis()-currentTimeMs)<visibleInterval){
                //just pending. I'm sure there's some better ways to do this
            }
            displayString=this.generateRandomString(this.round);
            flashSequence(displayString);
            this.playerTurn=true;
            drawFrame("");
            inputString=this.solicitNCharsInput(this.round);
            if (inputString.equals(displayString)){
                this.round++;
            }
            else {
                gameOver=true;
            }
        }
        //show game over message
        drawFrame("Game Over! You made it to round:"+round);
    }

}
