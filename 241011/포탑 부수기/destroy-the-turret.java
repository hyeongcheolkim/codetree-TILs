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

    static class Pos{
        int row;
        int col;

        Pos(int row, int col){
            this.row = row;
            this.col = col;
        }

        @Override
        public int hashCode(){
            return Objects.hash(this.row, this.col);
        }

        @Override
        public String toString(){
            return String.format("[r:%d,c:%d]",row,col);
        }

        @Override
        public boolean equals(Object obj){
            Pos that = (Pos) obj;
            return this.row == that.row && this.col == that.col;
        }
    }

    int n, m, k;
    int[][] board;
    Map<Pos, Integer> recentTurns = new HashMap<>();
    int cnt = 0;

    Pos getWeakestTower(){
        List<Pos> targets = new ArrayList<>();
        int miniPower = Integer.MAX_VALUE / 2;

        for(int i=0;i<n;++i)
            for(int j=0;j<m;++j){
                int power = board[i][j];
                if(power == 0)
                    continue;
                if(power > miniPower)
                    continue;
                if(power == miniPower){
                    targets.add(new Pos(i, j));
                    continue;
                }
                if(power < miniPower){
                    miniPower = power;
                    targets = new ArrayList<>();
                    targets.add(new Pos(i, j));
                    continue;
                }
            }
        if(targets.size() == 0)
            return new Pos(0,0);
        if(targets.size() == 1)
            return targets.get(0);
        
        targets.sort((Pos a, Pos b)->{
            int recentTurnA = recentTurns.getOrDefault(a, 0);
            int recentTurnB = recentTurns.getOrDefault(b, 0);

            if(recentTurnA != recentTurnB)
                return recentTurnB - recentTurnA;
            
            int rcSumA = a.row + a.col;
            int rcSumB = b.row + b.col;

            if(rcSumA != rcSumB)
                return rcSumB - rcSumA;
            
            return b.col - a.col;
        });
        return targets.get(0);
    }

    Pos getStrongestTower(){
        List<Pos> targets = new ArrayList<>();   
        int maxiPower = -1;

        for(int i=0;i<n;++i)
            for(int j=0;j<m;++j){
                int power = board[i][j];
                if(power == 0)
                    continue;
                if(power < maxiPower)
                    continue;
                if(power == maxiPower){
                    targets.add(new Pos(i, j));
                    continue;
                }
                if(power > maxiPower){
                    maxiPower = power;
                    targets = new ArrayList<>();
                    targets.add(new Pos(i, j));
                    continue;
                }
            }
        if(targets.size() == 0)
            return new Pos(0,0);
        if(targets.size() == 1)
            return targets.get(0);
        
        targets.sort((Pos a, Pos b)->{
            int recentTurnA = recentTurns.getOrDefault(a, 0);
            int recentTurnB = recentTurns.getOrDefault(b, 0);

            if(recentTurnA != recentTurnB)
                return recentTurnA - recentTurnB;
            
            int rcSumA = a.row + a.col;
            int rcSumB = b.row + b.col;

            if(rcSumA != rcSumB)
                return rcSumA - rcSumB;
            
            return a.col - b.col;
        });
        return targets.get(0);
    }

    static int[] dr = new int[]{0,+1,0,-1,-1,-1,+1,+1};
    static int[] dc = new int[]{+1,0,-1,0,+1,-1,+1,-1};
    Deque<Pos> trace;
    List<Pos> traceResult;
    boolean[][] vst;

    void dfs(int row, int col, Pos target){
        if(traceResult == null && target.row == row && target.col == col){
            traceResult = new ArrayList<>(trace);
            return;
        }
        if(traceResult != null && trace.size() < traceResult.size() && target.row == row && target.col == col){
            traceResult = new ArrayList<>(trace);
            return;
        }
        if(traceResult != null && traceResult.size() <= trace.size()){
            return;
        }
        for(int direction=0;direction<4;++direction){
            int nr = (row + dr[direction] + n) % n;
            int nc = (col + dc[direction] + m) % m;

            if(vst[nr][nc])
                continue;
            if(board[nr][nc] == 0)
                continue;

            vst[nr][nc] = true;
            trace.addLast(new Pos(nr, nc));
            dfs(nr, nc, target);
            trace.pollLast();
            vst[nr][nc] = false;
        }
    }

    void razorAttack(Pos from, Pos to){
        trace = new ArrayDeque<>();
        vst = new boolean[n][m];
        traceResult = null;

        vst[from.row][from.col] = true;
        dfs(from.row, from.col, to);
        if(traceResult == null)
            return;

        int damage = board[from.row][from.col];
        for(int i=0;i<traceResult.size();++i){
            Pos p = traceResult.get(i);
            if(board[p.row][p.col] == 0)
                continue;
            board[p.row][p.col] = Integer.max(0, board[p.row][p.col] - damage/2);
            if(board[p.row][p.col] == 0)
                --cnt;
        }

        board[to.row][to.col] = Integer.max(0, board[to.row][to.col] - damage);
        if(board[to.row][to.col] == 0)
            --cnt;

        List<Pos> blackList = new ArrayList<>(traceResult);
        blackList.add(from);
        blackList.add(to);
        repair(blackList);
    }

    void repair(List<Pos> blackList){
        for(int i=0;i<n;++i)
            for(int j=0;j<m;++j){
                if(blackList.contains(new Pos(i,j)))
                    continue;
                if(board[i][j] == 0)
                    continue;
                ++board[i][j];
            }
    }

    void bombAttack(Pos from, Pos to){
        int damage = board[from.row][from.col];
        int r = to.row;
        int c = to.col;
        
        List<Pos> blackList = new ArrayList<>();
        blackList.add(from);
        blackList.add(to);
        for(int direction=0;direction<8;++direction){
            int nr = (r + dr[direction] + n) % n;
            int nc = (c + dc[direction] + m) % m;
            if(board[nr][nc] == 0)
                continue;
            board[nr][nc] = Integer.max(0, board[nr][nc] - damage / 2);
            if(board[nr][nc] == 0)
                --cnt;
            blackList.add(new Pos(nr, nc));
        }
        if(board[r][c] == 0)
            return;
        board[r][c] = Integer.max(0, board[r][c] - damage);
        if(board[r][c] == 0)
            --cnt;
        
        repair(blackList);
    }

    void takeTurn(int nowTurn){
        Pos weakest = getWeakestTower();
        Pos strongest = getStrongestTower();

        board[weakest.row][weakest.col] += n + m;

        razorAttack(weakest, strongest);
        if(traceResult == null)
            bombAttack(weakest, strongest);

        recentTurns.put(weakest, nowTurn);
    }

    void solution(){
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
        n = input[0];
        m = input[1];
        k = input[2];

        board = new int[n][];
        for(int i=0;i<n;++i){
            board[i] = Arrays.stream(readLine().split(" "))
                            .limit(m)
                            .mapToInt(Integer::parseInt)
                            .toArray();
            for(int j=0;j<m;++j)
                if(board[i][j] != 0)
                    ++cnt;
        }

        for(int i=1;i<=k && cnt >= 2;++i){
            takeTurn(i);
        }

        Pos strongest = getStrongestTower();
        System.out.print(board[strongest.row][strongest.col]);
    }

    void printBoard(){
        for(int i=0;i<n;++i){
            for(int j=0;j<m;++j){
                System.out.print(board[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solution();
    }
}