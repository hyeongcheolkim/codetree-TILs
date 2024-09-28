import java.util.*;
import java.io.*;

public class Main {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int N, Q;
    int[] parents;
    int[] authorities;
    int[][] authorityCount;
    int[] messageCount;
    boolean[] alarms;
    final int MAX_DEPTH = 22;

    String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    void toggleAlarm(int initNodeNumber){
        if(!alarms[initNodeNumber]){
            int nowNode = parents[initNodeNumber];
            int authorityPower = 1;
            
            while(nowNode != 0){
                for(int i=authorityPower; i<=21; ++i){
                    messageCount[nowNode] += authorityCount[initNodeNumber][i];
                    if(i > authorityPower)
                        authorityCount[nowNode][i-authorityPower] += authorityCount[initNodeNumber][i];
                }
                if(!alarms[nowNode])
                    break;
                nowNode = parents[nowNode];
                ++authorityPower;
            }
            alarms[initNodeNumber] = true;
        }else{
            int nowNode = parents[initNodeNumber];
            int authorityPower = 1;
            
            while(nowNode != 0){
                for(int i=authorityPower; i<=21; ++i){
                    messageCount[nowNode] -= authorityCount[initNodeNumber][i];
                    if(i > authorityPower)
                        authorityCount[nowNode][i-authorityPower] -= authorityCount[initNodeNumber][i];
                }
                if(!alarms[nowNode])
                    break;
                nowNode = parents[nowNode];
                ++authorityPower;
            }
            alarms[initNodeNumber] = false;
        }
    }

    void changeAuthority(int initNodeNumber, int nextAuthorityPower){
        int prevAuthorityPower = authorities[initNodeNumber];
        nextAuthorityPower = Math.min(nextAuthorityPower, 20);
        authorities[initNodeNumber] = nextAuthorityPower;

        --authorityCount[initNodeNumber][prevAuthorityPower];
        if(alarms[initNodeNumber]){
            int nowNode = parents[initNodeNumber];
            int authorityPower = 1;
            while(nowNode != 0){
                if(prevAuthorityPower >= authorityPower)
                    --messageCount[nowNode];
                if(prevAuthorityPower > authorityPower)
                    --authorityCount[nowNode][prevAuthorityPower - authorityPower];
                if(!alarms[nowNode])
                    break;
                nowNode = parents[nowNode];
                ++authorityPower;
            }
        }

        ++authorityCount[initNodeNumber][nextAuthorityPower];
        if(alarms[initNodeNumber]){
            int nowNode = parents[initNodeNumber];
            int authorityPower = 1;
            while(nowNode != 0){
                if(nextAuthorityPower >= authorityPower)
                    ++messageCount[nowNode];
                if(nextAuthorityPower > authorityPower)
                    ++authorityCount[nowNode][nextAuthorityPower - authorityPower];
                if(!alarms[nowNode])
                    break;
                nowNode = parents[nowNode];
                ++authorityPower;
            }
        }
    }

    void changeParent(int initNodeNumber1, int initNodeNumber2){
        boolean prevNode1Alarm = alarms[initNodeNumber1];
        boolean prevNode2Alarm = alarms[initNodeNumber2];

        if(alarms[initNodeNumber1])
            toggleAlarm(initNodeNumber1);
        if(alarms[initNodeNumber2])
            toggleAlarm(initNodeNumber2);
        
        int tmpParent = parents[initNodeNumber1];
        parents[initNodeNumber1] = parents[initNodeNumber2];
        parents[initNodeNumber2] = tmpParent;

        if(prevNode1Alarm)
            toggleAlarm(initNodeNumber1);
        if(prevNode2Alarm)
            toggleAlarm(initNodeNumber2);
    }
    
    void solve(){
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
        N = input[0];
        Q = input[1];
        parents = new int[N+1];
        authorities = new int[N+1];
        authorityCount = new int[N+1][MAX_DEPTH];
        messageCount = new int[N+1];
        alarms = new boolean[N+1];
        Arrays.fill(alarms, true);

        for(int q=0;q<Q;++q){
            int[] line = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
            int oper = line[0];

            if(oper == 100){
                for(int i=1;i<=N;++i)
                    parents[i] = line[i];
                for(int i=1;i<=N;++i)
                    authorities[i] = Math.min(20, line[i+N]);
                
                for(int i=1;i<=N;++i){
                    int nowNode = i;
                    int nowAuthority = authorities[i];
                    ++authorityCount[nowNode][nowAuthority];

                    while(parents[nowNode] != 0 && nowAuthority != 0){
                        nowNode = parents[nowNode];
                        --nowAuthority;
                        if(nowAuthority != 0){
                            ++authorityCount[nowNode][nowAuthority];
                        }
                        ++messageCount[nowNode];
                    }
                }
            }
            if(oper == 200){
                int c = line[1];
                toggleAlarm(c);
            }
            if(oper == 300){
                int c = line[1];
                int power = line[2];
                changeAuthority(c, power);
            }
            if(oper == 400){
                int c1 = line[1];
                int c2 = line[2];
                changeParent(c1, c2);
            }
            if(oper == 500){
                int c = line[1];
                System.out.println(messageCount[c]);
            }                            
        }
    }

    void printAuthorityCount(){
        for(int i=1;i<=N;++i){
            for(int j=1;j<=5;++j)
                System.out.print(authorityCount[i][j] + " ");
            System.out.println();
        }
    }


    public static void main(String[] args) {
        Main m = new Main();
        m.solve();
    }
}