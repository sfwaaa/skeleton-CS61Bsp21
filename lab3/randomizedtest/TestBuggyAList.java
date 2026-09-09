package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        BuggyAList<Integer> buggyList=new BuggyAList<>();
        AListNoResizing<Integer> listNoResize=new AListNoResizing<>();

        buggyList.addLast(1);
        listNoResize.addLast(1);

        buggyList.addLast(2);
        listNoResize.addLast(2);

        buggyList.addLast(3);
        listNoResize.addLast(3);

        int bugRemove=0;
        int expected=0;
        bugRemove=buggyList.removeLast();
        expected=listNoResize.removeLast();
        assertEquals(expected,bugRemove);

        bugRemove=buggyList.removeLast();
        expected=listNoResize.removeLast();
        assertEquals(expected,bugRemove);

        bugRemove=buggyList.removeLast();
        expected=listNoResize.removeLast();
        assertEquals(expected,bugRemove);
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyList=new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            switch (operationNumber){
                case  0://addLast
                    int randVal = StdRandom.uniform(0, 100);
                    L.addLast(randVal);
                    buggyList.addLast(randVal);
                    System.out.println("addLast(" + randVal + ")");
                    break;
                case  1:// size
                    int size = L.size();
                    assertEquals(size,buggyList.size());
                    System.out.println("size: " + size);
                    break;
                case  2://getLast
                    if (L.size()==0){
                        System.out.println("No value in list");
                        break;
                    }
                    int lastVal=L.getLast();
                    int buggyLast=buggyList.getLast();
                    assertEquals(lastVal,buggyLast);
                    System.out.println("getLast(" + lastVal + ")");
                    break;
                case  3:
                    if (L.size()==0){
                        System.out.println("No value in list");
                        break;
                    }
                    int deleted =L.removeLast();
                    int buggyRemove=buggyList.removeLast();
                    assertEquals(deleted,buggyRemove);
                    System.out.println("removeLast(" + deleted + ")");
                    break;
                default:
                    System.out.println("THIS LINE SHOULDN'T BE REACHED.");
                    break;
            }
        }
    }
}
