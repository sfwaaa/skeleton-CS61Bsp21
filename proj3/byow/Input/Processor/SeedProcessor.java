package byow.Input.Processor;

//process input seed string
public class SeedProcessor {
    private final StringBuilder digitQueue;
    public SeedProcessor(){
        digitQueue=new StringBuilder();
    }
    public void feed(Character digit){
        digitQueue.append(digit);
    }
    /**
     * @return the input seed,-1 if invalid input
     */
    public long getSeed(){
        long seed;
        try {
            seed=Long.parseLong(digitQueue.toString(),10);
        }
        catch (NumberFormatException e){
            seed=-1;
        }
        finally {
            this.digitQueue.setLength(0);
        }
        return seed;
    }

    public String getSeedString(){
       return digitQueue.toString();
    }
}
