package deque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/*test class for ArrayDeque*/
public class ArrayDequeTest {
    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> lld1 = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        int removedItem=lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
        assertEquals(10,removedItem);

        assertNull(null,lld1.removeFirst());
        assertNull(null,lld1.removeLast());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {
        ArrayDeque<String>  lld1 = new ArrayDeque<String>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

    @Test
    public void iteratorTest(){
        ArrayDeque<Integer> list=new ArrayDeque<>();
        ArrayList<Integer> correctList=new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            int val= StdRandom.uniform(0, 1000);
            list.addLast(val);
            correctList.add(val);
        }

        int i=0;
        for(int item : list){
            assertEquals((long)correctList.get(i),(long)item);
            i++;
        }
    }
    @Test
    public void equalTest(){
        ArrayDeque<Integer> list=new ArrayDeque<>();
        ArrayDeque<Integer> other=new ArrayDeque<>();
        assertFalse(list.equals(null));
        assertFalse(list.equals("string"));

        for (int i = 0; i < 1000; i++) {
            int val= StdRandom.uniform(0, 1000);
            list.addLast(val);
            other.addLast(val);
        }
        assertTrue(list.equals(other));

        other.addLast(77);
        assertFalse(list.equals(other));
    }
    @Test
    public void randomizedTest(){
        ArrayDeque<Integer> list=new ArrayDeque<>();
        ArrayList<Integer> correctList=new ArrayList<>();

        int N = 50000;
        int randVal=0;
        int deleted=0;
        int buggyRemove=0;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);
            switch (operationNumber) {
                case 0://isEmpty
                    assertEquals(list.isEmpty(),correctList.isEmpty());
                    System.out.println("isEmpty: "+correctList.isEmpty() );
                    break;

                case 1:// size
                    int size = correctList.size();
                    assertEquals(size, list.size());
                    System.out.println("size: " + size);
                    break;
                case 2://get
                    if (correctList.size() == 0) {
                        System.out.println("No value in list");
                        break;
                    }
                    int randIndex = StdRandom.uniform(0, correctList.size());
                    int val = correctList.get(randIndex);
                    int lval = list.get(randIndex);
                    System.out.println("get(" + val + ") at"+randIndex);
                    assertEquals(val, lval);

                    break;
                case 3://addFirst
                    randVal = StdRandom.uniform(0, 100);
                    correctList.add(0,randVal);
                    list.addFirst(randVal);
                    System.out.println("addFirst(" + randVal + ")");
                    break;
                case 4://addLast
                    randVal = StdRandom.uniform(0, 100);
                    correctList.add(randVal);
                    list.addLast(randVal);
                    System.out.println("addLast(" + randVal + ")");
                    break;
                case 5://removeLast
                    if (correctList.size() == 0) {
                        System.out.println("No value in list");
                        break;
                    }
                    deleted = correctList.remove(correctList.size()-1);
                    buggyRemove = list.removeLast();
                    assertEquals(deleted, buggyRemove);
                    System.out.println("removeLast(" + deleted + ")");
                    break;
                case 6://removeFirst
                    if (correctList.size() == 0) {
                        System.out.println("No value in list");
                        break;
                    }
                    deleted = correctList.remove(0);
                    buggyRemove = list.removeFirst();
                    assertEquals(deleted, buggyRemove);
                    System.out.println("removeFirst(" + deleted + ")");
                    break;
                default:
                    System.out.println("THIS LINE SHOULDN'T BE REACHED.");
                    break;
            }
        }
    }
}
