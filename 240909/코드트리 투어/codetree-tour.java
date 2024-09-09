import java.util.*;
import java.io.*;
import java.util.stream.*;

public class Main {

    static class Product {
        public int id;
        public int revenue;
        public int dest;
        public int income;

        public Product(int id, int revenue, int dest, int income) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.income = income;
        }

        @Override
        public String toString() {
            return String.format("[id=%d, revenue=%d, dest=%d, income=%d]", id, revenue, dest, income);
        }
    }

    private static int INF = Integer.MAX_VALUE / 3;

    private int[] calculateCost() {
        int[] cost = new int[n];
        Arrays.fill(cost, INF);
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(x -> x[1]));

        pq.add(new int[] { startCity, 0 });
        cost[startCity] = 0;

        while (!pq.isEmpty()) {
            int[] p = pq.poll();
            int in = p[0];
            int inCost = p[1];

            if (cost[in] < inCost)
                continue;

            for (int[] nowNode : graph.get(in)) {
                int out = nowNode[0];
                int outCost = nowNode[1];

                int totalCost = inCost + outCost;
                if (cost[out] > totalCost) {
                    cost[out] = totalCost;
                    pq.add(new int[] { out, totalCost });
                }
            }
        }
        return cost;
    }

    private int Q, n, m;
    private int startCity = 0;

    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private List<List<int[]>> graph;
    private TreeMap<Integer, Product> productMap = new TreeMap<>();
    private boolean[] isExistProduct = new boolean[30000 + 1];

    private String readLine() {
        try {
            return br.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void read() {
        Q = Integer.parseInt(readLine());
    }

    private void solve() {
        int[] cost = null;
        while (Q-- != 0) {
            int[] input = Arrays.stream(readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            int oper = input[0];

            if (oper == 100) {
                n = input[1];
                m = input[2];

                graph = Stream.generate(() -> new ArrayList<int[]>()).limit(n).collect(Collectors.toList());

                for (int i = 3; i < input.length; i += 3) {
                    int v = input[i];
                    int u = input[i + 1];
                    int w = input[i + 2];

                    graph.get(v).add(new int[] { u, w });
                    graph.get(u).add(new int[] { v, w });
                }
                cost = calculateCost();
            }
            if (oper == 200) {
                int id = input[1];
                int revenue = input[2];
                int dest = input[3];
                int income = (cost[dest] == INF ? INF : revenue - cost[dest]);

                Product product = new Product(id, revenue, dest, income);
                productMap.put(id, product);
                isExistProduct[id] = true;
            }
            if (oper == 300) {
                int id = input[1];
                productMap.remove(id);
                isExistProduct[id] = false;
            }
            if (oper == 400) {
                boolean flag = true;
                List<Product> tmp = new ArrayList<>();

                // 우선순위가 높은 상품 찾기 (이득이 가장 큰 상품)
                for (Product product : productMap.values()) {
                    if (!isExistProduct[product.id])
                        continue;
                    if (product.income == INF || product.income < 0) {
                        tmp.add(product);
                        continue;
                    }
                    isExistProduct[product.id] = false;
                    System.out.println(product.id);
                    productMap.remove(product.id);
                    flag = false;
                    break;
                }
                if (flag) {
                    System.out.println(-1);
                }
            }
            if (oper == 500) {
                startCity = input[1];
                cost = calculateCost();

                // 출발지 변경에 따른 상품 업데이트
                for (Product product : productMap.values()) {
                    if (isExistProduct[product.id]) {
                        int income = (cost[product.dest] == INF ? INF : product.revenue - cost[product.dest]);
                        product.income = income;
                    }
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