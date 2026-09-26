package bstmap;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/** Tests by Brendan Hu, Spring 2015, revised for 2016 by Josh Hug */
public class TestBSTMap {

  	@Test
    public void sanityGenericsTest() {
    	try {
    		BSTMap<String, String> a = new BSTMap<String, String>();
	    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
	    	BSTMap<Integer, String> c = new BSTMap<Integer, String>();
	    	BSTMap<Boolean, Integer> e = new BSTMap<Boolean, Integer>();
	    } catch (Exception e) {
	    	fail();
	    }
    }

    //assumes put/size/containsKey/get work
    @Test
    public void sanityClearTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1+i);
            //make sure put is working via containsKey and get
            assertTrue( null != b.get("hi" + i) && (b.get("hi"+i).equals(1+i))
                        && b.containsKey("hi" + i));
        }
        assertEquals(455, b.size());
        b.clear();
        assertEquals(0, b.size());
        for (int i = 0; i < 455; i++) {
            assertTrue(null == b.get("hi" + i) && !b.containsKey("hi" + i));
        }
    }

    // assumes put works
    @Test
    public void sanityContainsKeyTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertFalse(b.containsKey("waterYouDoingHere"));
        b.put("waterYouDoingHere", 0);
        assertTrue(b.containsKey("waterYouDoingHere"));
    }

    // assumes put works
    @Test
    public void sanityGetTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(null,b.get("starChild"));
        assertEquals(0, b.size());
        b.put("starChild", 5);
        assertTrue(((Integer) b.get("starChild")).equals(5));
        b.put("KISS", 5);
        assertTrue(((Integer) b.get("KISS")).equals(5));
        assertNotEquals(null,b.get("starChild"));
        assertEquals(2, b.size());
    }

    // assumes put works
    @Test
    public void sanitySizeTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(0, b.size());
        b.put("hi", 1);
        assertEquals(1, b.size());
        for (int i = 0; i < 455; i++)
            b.put("hi" + i, 1);
        assertEquals(456, b.size());
    }

    //assumes get/containskey work
    @Test
    public void sanityPutTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        b.put("hi", 1);
        assertTrue(b.containsKey("hi") && b.get("hi") != null);
    }

    //assumes put works
    @Test
    public void containsKeyNullTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        b.put("hi", null);
        assertTrue(b.containsKey("hi"));
    }

    @Test
    public void RandomizedTest() {
          Map61B<Integer,Character> bst=new BSTMap<>();
          Map<Integer,Character> ull=new TreeMap<>();
          int N=10000;
        //Random rand=new Random();
        int op=0;
        int charNum=0;
        String str= "ajindakjdgaoijfiefpq98purq3oijqfnmdvkknfdokfml;s,fmdf;oirjew[0fij9dmaifp0e=093q2";
        String str1= "ajindakjdgaoijfiefpq98purq3oijqfnmdvkknfdokfml;s,fmdf;oirjew[0fij9dmaifp0e=093q2";
        String str2= "ajinakaedgaoijfinmlq9sjkc83oijqfnmdvkknfdokfml;s,fmdf;oirjew[0fij9dmhkfp0e=093q2";
        for (int i = 0; i < N; i++) {
            op= StdRandom.uniform(0, 6);
            charNum=StdRandom.uniform(0,str.length());
            switch (op){
                case 0://containsKey
                    System.out.println
                            ("contains("+str.charAt(charNum)+")="+ull.containsKey(charNum));
                    assertEquals
                            (ull.containsKey(charNum),bst.containsKey(charNum));
                    break;
                case 1://get
                    if (!ull.containsKey(charNum)){
                        break;
                    }
                    System.out.println
                            ("get ("+ charNum +")="+ull.get(charNum));
                    assertEquals
                            (ull.get(charNum),bst.get(charNum));
                    break;
                case 2://size
                    System.out.println
                            ("size ="+ull.size());
                    assertEquals
                            (ull.size(),bst.size());
                    break;
                case 3://put
                    System.out.println
                            ("put( "+charNum+","+str.charAt(charNum)+")");
                    ull.put(charNum,str.charAt(charNum));
                    bst.put(charNum,str.charAt(charNum));
                    break;
                case 4:
                    str=str2;
                    break;
                case 5:
                    str=str1;
                    break;
                default:
                    System.out.println("Shouldn't reach here");
                    break;
            }
        }
    }
}
