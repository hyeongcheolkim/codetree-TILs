import java.util.*;
import java.io.*;

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
    }

    private static int INF = Integer.MAX_VALUE / 3;
    private int Q, n, m;
    private int startCity = 0;

    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private List<List<int[]>> graph;
    private PriorityQueue<Product> pq;
    private Map<Integer, Product> productMap = new HashMap<>();
    private int[] cost;

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

    private int[] calculateCost() {
        int[] cost = new int[n];
        Arrays.fill(cost, INF);
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(x -> x[1]));

        pq.add(new int[]{startCity, 0});
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
                    pq.add(new int[]{out, totalCost});
                }
            }
        }
        return cost;
    }

    private void solve() {
        pq = new PriorityQueue<>((a, b) -> {
            if (a.income == b.income)
                return a.id - b.id;
            return b.income - a.income;
        });

        while (Q-- != 0) {
            int[] input = Arrays.stream(readLine().split(" "))
                                .mapToInt(Integer::parseInt)
                                .toArray();
            int oper = input[0];

            if (oper == 100) {
                n = input[1];
                m = input[2];

                graph = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    graph.add(new ArrayList<>());
                }

                for (int i = 3; i < input.length; i += 3) {
                    int v = input[i];
                    int u = input[i + 1];
                    int w = input[i + 2];

                    graph.get(v).add(new int[]{u, w});
                    graph.get(u).add(new int[]{v, w});
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
                pq.add(product);
            }

            if (oper == 300) {
                int id = input[1];
                productMap.remove(id);
            }

            if (oper == 400) {
                while (!pq.isEmpty()) {
                    Product p = pq.poll();
                    if (!productMap.containsKey(p.id))
                        continue;
                    if (p.income == INF || p.income < 0) {
                        continue;
                    }
                    productMap.remove(p.id);
                    System.out.println(p.id);
                    break;
                }
                if (pq.isEmpty()) {
                    System.out.println(-1);
                }
            }

            if (oper == 500) {
                startCity = input[1];
                cost = calculateCost();
                for (Product p : productMap.values()) {
                    p.income = (cost[p.dest] == INF ? INF : p.revenue - cost[p.dest]);
                    pq.add(p);
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