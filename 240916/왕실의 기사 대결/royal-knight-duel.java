import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    int L, N, Q;
    int[][] board;
    boolean[][] trapBoard;
    Map<Integer, Integer> health = new HashMap<>();
    Map<Integer, Integer> initHealth = new HashMap<>();
    Map<Integer, int[]> knightsHW = new HashMap<>();
    Map<Integer, int[]> knights = new HashMap<>();

    int[] dr = new int[]{-1,0,+1,0};
    int[] dc = new int[]{0,+1,0,-1};

    void fillBoard(int num, int r, int c, int h, int w){
        fillBoardWithVal(num, r, c, h, w, num);
    }

    void fillBoardWithVal(int num, int r, int c, int h, int w, int val){
        for(int i=r;i<r+h;++i)
            for(int j=c;j<c+w;++j){
                board[i][j] = val;
            }
    }

    void printBoard(){
        for(int i=1;i<=L;++i){
            for(int j=1;j<=L;++j){
                if(board[i][j] == -1)
                    System.out.print(0);
                else if(board[i][j] == -2)
                    System.out.print('X');
                else
                    System.out.print(board[i][j]);
                System.out.print(' ');
            }
            System.out.println();
        }
        System.out.println();
    }

    void printTrapBoard(){
        for(int i=1;i<=L;++i){
            for(int j=1;j<=L;++j){
                System.out.print(trapBoard[i][j] ? 'T' : 'O');
                System.out.print(' ');
            }
            System.out.println();
        }
        System.out.println();
    }

    int countTrap(int r, int c, int h, int w){
        int ret = 0;
        for(int i=r;i<r+h;++i)
            for(int j=c;j<c+w;++j)
                if(trapBoard[i][j])
                    ++ret;
        return ret;
    }

    boolean move(int n, int d, List<Integer> moveTargets){
        int[] knightsHWValue = knightsHW.get(n);
        int[] knightLocation = knights.get(n);

        int h = knightsHWValue[0];
        int w = knightsHWValue[1];
        int r = knightLocation[0];
        int c = knightLocation[1];

        int nr = r + dr[d];
        int nc = c + dc[d];

        List<int[]> targets = new ArrayList<>();
        if(d == 0){
            for(int i=c;i<c+w;++i)
                targets.add(new int[]{r-1, i});
        }
        if(d == 1){
            for(int i=r;i<r+h;++i)
                targets.add(new int[]{i, c+w});
        }
        if(d == 2){
            for(int i=c;i<c+w;++i)
                targets.add(new int[]{r+h, i});
        }
        if(d == 3){
            for(int i=r;i<r+h;++i)
                targets.add(new int[]{i, c-1});
        }

        for(int[] target : targets){
            int tr = target[0];
            int tc = target[1];

            if(!(1<=tr && tr<=L && 1<=tc && tc<=L))
                return false;
            if(board[tr][tc] == -2)
                return false;
        }

        boolean flag = true;
        for(int[] target : targets){
            int tr = target[0];
            int tc = target[1];

            if(board[tr][tc] >= 1 && !moveTargets.contains(board[tr][tc])){
                moveTargets.add(board[tr][tc]);
                flag &= move(board[tr][tc], d, moveTargets);
            }
        }

        return flag;
    }

    void solve(){
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
        L = input[0];
        N = input[1];
        Q = input[2];

        board = new int[L+1][];
        trapBoard = new boolean[L+1][L+1];
        for(int i=1;i<=L;++i){
            board[i] = IntStream.concat(
                IntStream.of(0),
                Arrays.stream(readLine().split(" ")).limit(L).mapToInt(x -> -Integer.parseInt(x))
            )
            .toArray();
        }
        for(int i=1;i<=L;++i)
            for(int j=1;j<=L;++j)
                trapBoard[i][j] = (board[i][j] == -1);
        for(int n=1;n<=N;++n){
            int[] line = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
            
            int r = line[0];
            int c = line[1];
            int h = line[2];
            int w = line[3];
            int k = line[4];

            knights.put(n, new int[]{r, c});
            knightsHW.put(n, new int[]{h, w});
            health.put(n, k);
            initHealth.put(n, k);
            fillBoard(n, r, c, h, w);
        }
        for(int x=0;x<Q;++x){
            int[] oper = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
            int i = oper[0];
            int d = oper[1];

            if(health.containsKey(i)){
                List<Integer> targetKnights = new ArrayList<>();
                targetKnights.add(i);
                boolean movable = move(i, d, targetKnights);

                if(!movable)
                    continue;
                
                for(int n : targetKnights){
                    int[] knightsHWValue = knightsHW.get(n);
                    int[] knightLocation = knights.get(n);

                    int h = knightsHWValue[0];
                    int w = knightsHWValue[1];
                    int r = knightLocation[0];
                    int c = knightLocation[1];
                    
                    fillBoardWithVal(n, r, c, h, w, 0);
                }
                for(int n : targetKnights){
                    int[] knightsHWValue = knightsHW.get(n);
                    int[] knightLocation = knights.get(n);

                    int h = knightsHWValue[0];
                    int w = knightsHWValue[1];
                    int r = knightLocation[0];
                    int c = knightLocation[1];

                    int nr = r + dr[d];
                    int nc = c + dc[d];

                    fillBoard(n, nr, nc, h, w);
                    knights.put(n, new int[]{nr, nc});
                    if(i == n)
                        continue;

                    int cnt = countTrap(nr, nc, h, w);
                    health.merge(n, -cnt, Integer::sum);
                    if(health.get(n) <= 0){
                        fillBoardWithVal(n, nr, nc, h, w, 0);
                        health.remove(n);
                    }
                }
            }
        }

        int damage = 0;
        for(Map.Entry<Integer,Integer> entry : health.entrySet()){
            damage += initHealth.get(entry.getKey()) - entry.getValue();
        }
        System.out.print(damage);
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solve();
    }
}

// 0위 1오른쪽 2아래쪽 3왼쪽