import java.util.*;
import java.io.*;

public class Main {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    int Q, L;
    Map<String, List<int[]>> sushis = new HashMap<>();
    Map<String, Integer> exitTime = new HashMap<>();
    Map<String, int[]> enterDetail = new HashMap<>();
    Map<String, Integer> sushiEa = new HashMap<>();

    List<String[]> queries = new ArrayList<>();

    void solve(){
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
        L = input[0];
        Q = input[1];

        for(int i=0;i<Q;++i){
            String[] line = readLine().split(" ");
            String oper = line[0];

            if(oper.equals("100")){
                int t = Integer.parseInt(line[1]);
                int x = Integer.parseInt(line[2]);
                String name = line[3];

                sushis.computeIfAbsent(name, (key) -> new ArrayList<int[]>())
                        .add(new int[]{t, x});
            }
            if(oper.equals("200")){
                int t = Integer.parseInt(line[1]);
                int x = Integer.parseInt(line[2]);
                String name = line[3];
                int n = Integer.parseInt(line[4]);

                enterDetail.put(name, new int[]{t, x});
                sushiEa.put(name, n);
            }
            if(oper.equals("300")){
                int t = Integer.parseInt(line[1]);
            }
            queries.add(line);
        }

        for(Map.Entry<String, List<int[]>> entry : sushis.entrySet()){
            String sushiOwnerName = entry.getKey();
            int[] detail = enterDetail.get(sushiOwnerName);
            int enterTime = detail[0];
            int enterLocation = detail[1];

            for(int[] sushiDetail : entry.getValue()){
                int sushiTime = sushiDetail[0];
                int sushiLocation = sushiDetail[1];

                if(sushiTime < enterTime){
                    int timeGap = enterTime - sushiTime;
                    int sushiTempLocation = (sushiLocation + timeGap)%L;
                    int locationGap = (enterLocation - sushiTempLocation + L)%L;
                    int eatTime = enterTime + locationGap;

                    queries.add(new String[]{"201", String.valueOf(eatTime), sushiOwnerName});
                    exitTime.merge(sushiOwnerName, eatTime, Integer::max);
                }
                if(sushiTime >= enterTime){
                    int locationGap = (enterLocation - sushiLocation + L)%L;
                    int eatTime = sushiTime + locationGap;

                    queries.add(new String[]{"201", String.valueOf(eatTime), sushiOwnerName});
                    exitTime.merge(sushiOwnerName, eatTime, Integer::max);
                }
            }
        }
        for(Map.Entry<String, Integer> entry : exitTime.entrySet()){
            String name = entry.getKey();
            int time = entry.getValue();

            queries.add(new String[]{"202", String.valueOf(time), name});
        }

        queries.sort((String[] a, String[] b) ->{
            int aOper = Integer.parseInt(a[0]);
            int aTime = Integer.parseInt(a[1]);
            int bOper = Integer.parseInt(b[0]);
            int bTime = Integer.parseInt(b[1]);

            if(aTime == bTime)
                return aOper - bOper;
            return aTime - bTime;
        });

        int humanCount = 0;
        int sushiCount = 0;
        for(String[] query : queries){
            String oper = query[0];

            if(oper.equals("100")){
                ++sushiCount;
            }
            if(oper.equals("200")){
                ++humanCount;
            }
            if(oper.equals("201")){
                --sushiCount;
            }
            if(oper.equals("202")){
                --humanCount;
            }
            if(oper.equals("300")){
                System.out.println(String.format("%d %d", humanCount, sushiCount));
            }
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solve();
    }
}