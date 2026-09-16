package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static final GuitarString[] GUITAR_STRINGS=new GuitarString[37];
    public static void main(String[] args){
        initGuitarStrings();
        GuitarString gstr=null;
        int charIndex=0;
        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                charIndex=GuitarHero.KEYBOARD.indexOf(key);
                if(charIndex<0){
                    continue;
                }
                gstr=GuitarHero.GUITAR_STRINGS[charIndex];
                gstr.pluck();
            }

            /* compute the superposition of samples */
            double sample = 0;
            for(GuitarString str : GuitarHero.GUITAR_STRINGS){
                sample+=str.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for(GuitarString str : GuitarHero.GUITAR_STRINGS){
                str.tic();
            }
        }
    }
    private static void initGuitarStrings(){
        int length=GuitarHero.KEYBOARD.length();
        for (int i = 0; i < length; i++) {
            GuitarHero.GUITAR_STRINGS[i]=new GuitarString(
                    440*Math.pow(2, (double) (i - 24) /(double)12));
        }

    }
}
