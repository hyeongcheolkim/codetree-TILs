import java.util.*;
import java.io.*;
import java.util.stream.*;

public class Main {

    static class Product{
        public int id;
        public int revenue;
        public int dest;
        public int income;

        public Product(int id, int revenue, int dest, int income){
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.income = income;
        }

        @Override
        public String toString(){
            return String.format("[id=%d, revenue=%d, dest=%d, income=%d]", id, revenue, dest, income);
        }
    }

    private static int INF = Integer.MAX_VALUE / 3;

    private int[] calculateCost(){
        int[] cost = new int[n];
        Arrays.fill(cost, INF);
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(x -> x[1]));

        pq.add(new int[]{startCity, 0});
        cost[startCity] = 0;

        while(!pq.isEmpty()){
            int[] p = pq.poll();
            int in = p[0];
            int inCost = p[1];

            if(cost[in] < inCost)
                continue;
                
            for(int[] nowNode : graph.get(in)){
                int out = nowNode[0];
                int outCost = nowNode[1];

                int totalCost = inCost + outCost;
                if(cost[out] > totalCost){
                    cost[out] = totalCost;
                    pq.add(new int[]{out, totalCost});
                }
            }
        }
        return cost;
    }

    private int Q, n, m;
    private int startCity = 0;

    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private List<List<int[]>> graph;
    private List<Product> products = new ArrayList<>();
    private boolean[] isExistProduct = new boolean[30000 + 1];

    private String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private void read(){
        Q = Integer.parseInt(readLine());
    }

    private void solve(){
        int[] cost = null;
        while(Q-- != 0){
            int[] input = Arrays.stream(readLine().split(" "))
                                .mapToInt(Integer::parseInt)
                                .toArray();
            int oper = input[0];

            if(oper == 100){
                n = input[1];
                m = input[2];
                
                graph = Stream.generate(() -> {return new ArrayList<int[]>();})
                            .limit(n)
                            .collect(Collectors.toList());

                for(int i=3; i<input.length; i+=3){
                    int v = input[i];
                    int u = input[i+1];
                    int w = input[i+2];

                    graph.get(v).add(new int[]{u, w});
                    graph.get(u).add(new int[]{v, w});
                }
                cost = calculateCost();
                
            }
            if(oper == 200){
                int id = input[1];
                int revenue = input[2];
                int dest = input[3];
                int income = (cost[dest] == INF ? INF : revenue - cost[dest]);

                products.add(new Product(id, revenue, dest, income));
                isExistProduct[id] = true;
            }
            if(oper == 300){
                int id = input[1];
                isExistProduct[id] = false;
            }
            if(oper == 400){
                boolean flag = true;
                List<Product> tmp = new ArrayList<>();

                // 유효한 상품만 남겨두기 위해 필터링
                products = products.stream()
                                   .filter(p -> isExistProduct[p.id])
                                   .collect(Collectors.toList());
                
                products.sort((Product a, Product b) -> {
                    if(a.income == b.income)
                        return a.id - b.id;
                    return b.income - a.income;
                });

                while(!products.isEmpty()){
                    Product p = products.remove(0);
                    if(p.income == INF || p.income < 0){
                        tmp.add(p);
                        continue;
                    }
                    isExistProduct[p.id] = false;
                    System.out.println(p.id);
                    flag = false;
                    break;
                }

                // 판매 가능한 상품이 없을 때만 -1 출력
                if(flag) {
                    System.out.println(-1);
                }

                // 남은 상품 다시 리스트에 저장
                products.addAll(tmp);
            }
            if(oper == 500){
                List<Product> tmp = new ArrayList<>();
                for(Product p : products){
                    if(isExistProduct[p.id]){
                        tmp.add(p);
                    }
                }
                startCity = input[1];
                cost = calculateCost();

                products.clear();

                for(Product p : tmp){
                    int income = (cost[p.dest] == INF ? INF : p.revenue - cost[p.dest]);
                    Product newProduct = new Product(p.id, p.revenue, p.dest, income);
                    isExistProduct[p.id] = true;
                    products.add(newProduct);
                }
            }
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.read();
        m.solve();
    }
}