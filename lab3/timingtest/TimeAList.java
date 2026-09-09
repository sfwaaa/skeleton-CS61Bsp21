package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> dataSize=new AList<>();
        dataSize.addLast(1000);
        dataSize.addLast(2000);
        dataSize.addLast(8000);
        dataSize.addLast(16000);
        dataSize.addLast(32000);
        dataSize.addLast(64000);
        dataSize.addLast(128000);
        //if using multiplicative resizing
        //dataSize.addLast(12800000);
        //{1000,2000,8000,16000,32000,64000};

        AList<Double> times =new AList<Double>();

        int insertVal=1;
        Stopwatch timer =new Stopwatch();
        double previousTime=0;
        int currentSize=0;

        AList<Integer> list=new AList<Integer>();
        for (int sizeIndex=0;sizeIndex<dataSize.size();sizeIndex++){
            currentSize=dataSize.get(sizeIndex);
            previousTime=timer.elapsedTime();
            for(int i=0;i<currentSize;i++){
                list.addLast(insertVal);
            }
            times.addLast(timer.elapsedTime()-previousTime);
        }
        printTimingTable(dataSize,times,dataSize);
    }
}
