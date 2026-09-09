package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    public static void timeGetLast() {

        SLList<Integer> list=new SLList<>();

        AList<Integer> dataSize=new AList<>();
        dataSize.addLast(1000);
        dataSize.addLast(2000);
        dataSize.addLast(8000);
        dataSize.addLast(16000);
        dataSize.addLast(32000);
        dataSize.addLast(64000);
        dataSize.addLast(128000);
        dataSize.addLast(1280000);

        AList<Integer> counts=new AList<>();
        int callCount=10000;

        Stopwatch timer=new Stopwatch();
        double previousTime=0;
        AList<Double> times=new AList<>();

        int data=1;
        int dataCount=0;
        for (int i=0;i<dataSize.size();i++){
            //construct SLList
            dataCount=dataSize.get(i);
            for (int j=list.size();j<dataCount;j++){
                list.addFirst(data);
            }
            counts.addLast(callCount);
            previousTime=timer.elapsedTime();
            //call getLast for assigned times
            for (int k=0;k<callCount;k++){
                list.getLast();
            }
            times.addLast(timer.elapsedTime()-previousTime);
        }
        printTimingTable(dataSize,times,counts);
    }

}
