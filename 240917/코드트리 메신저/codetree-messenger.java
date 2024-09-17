import java.util.*;
import java.io.*;

public class Main {

    static class Node{
        int num;
        int parent = -1;
        Set<Integer> childs;

        public Node(int num, int parent, Set<Integer> childs){
            this.num = num;
            this.parent = parent;
            this.childs = new HashSet<>(childs);
        }

        public Node(int num, int parent){
            this.num = num;
            this.parent = parent;
            this.childs = new HashSet<>();
        }

        @Override
        public String toString(){
            return String.format("num:%d parent:%d childs:%s", num, parent, childs.toString());
        }
    }

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    Map<Integer, Boolean> alarms = new HashMap<>();
    Map<Integer, Integer> authorities = new HashMap<>();
    Map<Integer, Node> nodes = new HashMap<>();

    int N, Q;

    void changeAuthority(int num, int power){
        authorities.put(num, power);
    }

    void changeAlarm(int num){
        alarms.put(num, !alarms.get(num));
    }

    void printNodes(){
        for(Map.Entry<Integer, Node> e : nodes.entrySet()){
            int num = e.getKey();
            String additionalData = String.format("alarm: %b authority: %d", alarms.get(num), authorities.get(num));
            System.out.println(e.getValue() + " " + additionalData);
        }
    }

    void changeParent(int node1Num, int node2Num){
        Node node1 = nodes.get(node1Num);
        Node node2 = nodes.get(node2Num);

        int p1 = node1.parent;
        int p2 = node2.parent;

        node1.parent = p2;
        node2.parent = p1;

        Node parentNode1 = nodes.get(p1);
        Node parentNode2 = nodes.get(p2);
        
        parentNode1.childs.remove(node1Num);
        parentNode2.childs.remove(node2Num);

        parentNode1.childs.add(node2Num);
        parentNode2.childs.add(node1Num);
    }

    int alarmCount(int startNodeNum){
        int ret = 0;
        Node startNode = nodes.get(startNodeNum);

        int depth = 1;
        Queue<Integer> q = new ArrayDeque<>();

        for(int childNodeNum : startNode.childs)
            q.add(childNodeNum);
        while(!q.isEmpty()){
            Queue<Integer> tq = new ArrayDeque<>();

            while(!q.isEmpty()){
                int p = q.poll();
                Node nowNode = nodes.get(p);
                int authority = authorities.get(p);
                boolean alarm = alarms.get(p);
                // System.out.println(nowNode + "Authority:" + String.valueOf(authority) + "alarm:" + String.valueOf(alarm));

                if(!alarm)
                    continue;
                if(authority >= depth)
                    ++ret;
                    
                // System.out.println(nowNode);
                for(int nextNodeNum : nowNode.childs)
                    tq.add(nextNodeNum);
            }
            q = tq;
            ++depth;
        }
        return ret;
    }

    String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    
    void solve(){
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
        N = input[0];
        Q = input[1];


        Node mainNode = new Node(0, -1);
        nodes.put(0, mainNode);
        for(int i=0;i<=N;++i)
            alarms.put(i, true);
        for(int i=1;i<=N;++i){
            Node node = new Node(i, -1);
            nodes.put(i, node);
        }   

        for(int q=0;q<Q;++q){
            int[] line = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
            int oper = line[0];

            if(oper == 100){
                for(int i=1;i<=N;++i){
                    Node node = nodes.get(i);
                    node.parent = line[i];
        
                    nodes.get(node.parent).childs.add(i);
                }
                for(int i=N+1;i<N+1+N;++i){
                    int authority = line[i];
                    authorities.put(i-N, authority);
                }
            }
            if(oper == 200){
                changeAlarm(line[1]);
            }
            if(oper == 300){
                changeAuthority(line[1], line[2]);
            }
            if(oper == 400){
                int c1 = line[1];
                int c2 = line[2];

                changeParent(c1, c2);
            }
            if(oper == 500){
                System.out.println(alarmCount(line[1]));
            }
            // if(q == 5)
                // printNodes();
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.solve();
    }
}