import java.util.*;
import java.io.*;

public class Main {

    static class TreeScoreCalculator{
        private Node rootNode;
        private Map<Integer, Integer> vst = new HashMap<>();

        public TreeScoreCalculator(Node rootNode){
            this.rootNode = rootNode;
            dfs(rootNode);
        }

        private int dfs(Node node){
            int ret = 0;
            ret |= (1 << node.color);
            for(Node childNode : node.childNodes){
                ret |= dfs(childNode);
            }
            vst.put(node.id, ret);
            return ret;
        }

        private int score(){
            int ret = 0;
            for(int value : vst.values()){
                int cnt = 0;
                for(int color = 1; color <= 5; ++color){
                    cnt += (value & (1 << color)) > 0 ? 1 : 0;
                }
                ret += cnt * cnt;
            }
            return ret;
        }
    }

    static class Node{
        public int id;
        public int color;
        public int maxDepth;
        public List<Node> childNodes = new ArrayList<>();

        public Node(int id, int color, int maxDepth){
            this.id = id;
            this.color = color;
            this.maxDepth = maxDepth;
        }
    }

    private Map<Integer, Node> nodes = new HashMap<>();

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    static String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void changeColorCascade(Node node, int color){
        node.color = color;
        for(Node childNode : node.childNodes){
            changeColorCascade(childNode, color);
        }
    }

    private void solve(){
        int q = Integer.parseInt(readLine());
        Node rootNode = null;

        while(q-- != 0){
            int[] input = Arrays.stream(readLine().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
            if(input[0] == 100){
                int m_id = input[1];
                int p_id = input[2];
                int color = input[3];
                int max_depth = input[4];

                if(p_id == -1){
                    Node node = new Node(m_id, color, max_depth);
                    nodes.put(m_id, node);
                    rootNode = node;
                    continue;
                }
                Node parentNode = nodes.get(p_id);

                if(parentNode == null || parentNode.maxDepth < 2)
                    continue;

                Node node = new Node(m_id, color, Integer.min(parentNode.maxDepth - 1, max_depth));
                parentNode.childNodes.add(node);
                nodes.put(m_id, node);
            }
            if(input[0] == 200){
                int m_id = input[1];
                int color = input[2];

                Node node = nodes.get(m_id);
                changeColorCascade(node, color);
            }
            if(input[0] == 300){
                int m_id = input[1];

                System.out.println(nodes.get(m_id).color);
            }
            if(input[0] == 400){
                TreeScoreCalculator tsc = new TreeScoreCalculator(rootNode);
                System.out.println(tsc.score());
            }
        }
    }

    public static void main(String[] args) {
        new Main().solve();
    }
}