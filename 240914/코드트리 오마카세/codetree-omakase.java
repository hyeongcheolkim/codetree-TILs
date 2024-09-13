import java.util.*;
import java.io.*;
import java.util.stream.*;

public class Main {


    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private int L, Q;
    private Map<String, List<int[]>> sushi = new HashMap<>();
    private Map<String, int[]> seat = new HashMap<>();
    private Map<String, Integer> ea = new HashMap<String, Integer>();

    private void printData(){
        System.out.println("sushi");
        for(Map.Entry<String, List<int[]>> e : sushi.entrySet()){
            for(int[] d : e.getValue()){
                System.out.println(e.getKey() + " " + Arrays.toString(d));
            }
        }
        System.out.println("seat");
        for(Map.Entry<String, int[]> e : seat.entrySet()){
            System.out.println(e.getKey() + " " + Arrays.toString(e.getValue()));
        }
        System.out.println("ea");
        for(Map.Entry<String, Integer> e : ea.entrySet()){
            System.out.println(e.getKey() + " " + String.valueOf(e.getValue()));
        }
    }

    public String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public int calculateLocation(int time, int location){
        int val = location - (time % L);
        if(val >= 0)
            return val;
        return L + val;
    }

    public void solve(){
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
        L = input[0];
        Q = input[1];

        sushi = new HashMap<String, List<int[]>>();
        seat = new HashMap<String, int[]>();

        int sushiSize = 0;

        while(Q-- != 0){
            String[] line = readLine().split(" ");

            if(line[0].equals("100")){
                int t = Integer.parseInt(line[1]);
                int x = Integer.parseInt(line[2]);
                String name = line[3];

                int location = calculateLocation(t, x);
                sushi.computeIfAbsent(name, (placeHolder)-> new ArrayList<int[]>())
                    .add(new int[]{t, x});
                ++sushiSize;
            }
            if(line[0].equals("200")){
                int t = Integer.parseInt(line[1]);
                int x = Integer.parseInt(line[2]);
                String name = line[3];
                int n = Integer.parseInt(line[4]);

                int location = calculateLocation(t, x);
                seat.put(name, new int[]{t, x});
                ea.put(name, n);
            }
            if(line[0].equals("300")){
                int time = Integer.parseInt(line[1]);
                int humanSize = ea.size();
                int tempSushiSize = sushiSize;
                List<String> removeTarget = new ArrayList<>();
                for(Map.Entry<String, int[]> e : seat.entrySet()){
                    int cnt = 0;
                    String name = e.getKey();
                    int sushiEa = ea.get(name);
                    int[] seatData = e.getValue();
                    int seatT = seatData[0];
                    int seatX = seatData[1];

                    List<int[]> sushiDataList = sushi.getOrDefault(name, new ArrayList<int[]>());

                    for(int[] sushiData : sushiDataList){
                        int sushiT = sushiData[0];
                        int sushiX = sushiData[1];

                        if(sushiT < seatT){
                            int sushiLoc = (sushiX + (seatT - sushiT))%L;
                            int dist = (seatX - sushiLoc + L)%L;
                            // System.out.println(String.format("%s %d %d %d  [%d %d]",name, sushiLoc, dist, time, sushiX, sushiT));
                            if(dist <= time - seatT)
                                ++cnt;
                        }
                        if(sushiT > seatT){
                            int dist = (sushiX - seatX + L)%L;
                            // System.out.println(String.format("%s %d %d  [%d %d]",name, dist, time, sushiX, sushiT));
                            if(dist <= time - sushiT)
                                ++cnt;
                        }
                    }
                    // System.out.println("cnt:"+String.valueOf(cnt));
                    tempSushiSize -= Integer.min(sushiEa, cnt);
                    if(cnt >= sushiEa){
                        --humanSize;
                        sushiSize -= sushiEa;
                        removeTarget.add(name);
                    }
                }
                for(String n : removeTarget){
                    ea.remove(n);
                    seat.remove(n);
                    sushi.remove(n);
                }
                // printData();
                System.out.println(String.format("%d %d", humanSize, tempSushiSize));
            }
        }

    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solve();
    }
}