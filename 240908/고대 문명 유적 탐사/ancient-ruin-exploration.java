import java.util.*;
import java.io.*;
import java.util.stream.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private int k, m;
    private int[][] board;
    private Deque<Integer> task = new ArrayDeque<>();

    private static int[] dr = new int[]{0,0,+1,-1};
    private static int[] dc = new int[]{+1,-1,0,0};

    private void read(){
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
        k = input[0];
        m = input[1];

        board = new int[5][5];
        for(int i = 0; i<5;++i){
            int[] line = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
            for(int j=0; j<5;++j)
                board[i][j] = line[j];
        }
        int[] tmp = Arrays.stream(readLine().split(" "))
                        .mapToInt(Integer::parseInt)
                        .limit(m)
                        .toArray();
        for(int val : tmp)
            task.addLast(val);
    }

    private void print(int[][] data){
        for(int i=0;i<data.length;++i){
            for(int j=0;j<data[i].length;++j)
                System.out.print(String.format("%d ", data[i][j]));
            System.out.println();
        }
        System.out.println();
    }

    private int[][] turnClock(int row, int col, int[][] origin){
        int[][] ret = new int[origin.length][];
        for(int i=0; i<origin.length; ++i)
            ret[i] = origin[i].clone();

        ret[row-1][col-1] = origin[row+1][col-1];
        ret[row-1][col] = origin[row][col-1];
        ret[row-1][col+1] = origin[row-1][col-1];

        ret[row][col-1] = origin[row+1][col];
        ret[row][col+1] = origin[row-1][col];

        ret[row+1][col-1] = origin[row+1][col+1];
        ret[row+1][col] = origin[row][col+1];
        ret[row+1][col+1] = origin[row-1][col+1];

        return ret;
    }

    private int[][] turnCounterClock(int row, int col, int[][] origin){
        int[][] ret = new int[origin.length][];
        for(int i=0; i<origin.length; ++i)
            ret[i] = origin[i].clone();

        ret[row-1][col-1] = origin[row-1][col+1];
        ret[row-1][col] = origin[row][col+1];
        ret[row-1][col+1] = origin[row+1][col+1];

        ret[row][col-1] = origin[row-1][col];
        ret[row][col+1] = origin[row+1][col];

        ret[row+1][col-1] = origin[row-1][col-1];
        ret[row+1][col] = origin[row][col-1];
        ret[row+1][col+1] = origin[row+1][col-1];

        return ret;
    }

    static class ScoreDetail{
        public int score;
        public List<int[]> pos;
        public int[][] nextBoard;

        ScoreDetail(int score, List<int[]> pos, int[][] nextBoard){
            this.score = score;
            this.pos = pos;
            this.nextBoard = nextBoard;
        }
    }

    private ScoreDetail score(int[][] target){
        boolean vst[][] = new boolean[5][5];
        List<int[]> retPos = new ArrayList<>();

        for(int i=0;i<5;++i){
            for(int j=0;j<5;++j){
                if(vst[i][j])
                    continue;
                Queue<int[]> q = new LinkedList<>();
                q.add(new int[]{i, j});
                vst[i][j] = true;
                List<int[]> pos = new ArrayList<>();
                pos.add(new int[]{i, j});

                while(!q.isEmpty()){
                    int[] p = q.poll();

                    int r = p[0];
                    int c = p[1];
                    int nowId = target[r][c];

                    for(int direction=0; direction<4; ++direction){
                        int nr = r + dr[direction];
                        int nc = c + dc[direction];

                        if(!(0<=nr && nr<5 && 0<=nc && nc<5))
                            continue;
                        if(vst[nr][nc])
                            continue;
                        if(target[nr][nc] != nowId)
                            continue;
                        q.add(new int[]{nr,nc});
                        vst[nr][nc] = true;
                        pos.add(new int[]{nr, nc});
                    }
                }
                if(pos.size() >= 3)
                    retPos.addAll(pos);
            }
        }
        return new ScoreDetail(retPos.size(), retPos, target);
    }

    private void dfs(int level, int[][] target){
        if(level == k){
            return;
        }
        int score = 0;
        List<ScoreDetail> score90Details = new ArrayList<>();
        List<ScoreDetail> score180Details = new ArrayList<>();
        List<ScoreDetail> score270Details = new ArrayList<>();

        for(int j=1;j<=3;++j){
            for(int i=1;i<=3;++i){
                ScoreDetail clock90ScoreDetail = score(turnClock(i, j, target));
                ScoreDetail clock180ScoreDetail = score(turnClock(i,j, turnClock(i,j, target)));
                ScoreDetail clock270ScoreDetail = score(turnCounterClock(i, j, target));

                score90Details.add(clock90ScoreDetail);
                score180Details.add(clock180ScoreDetail);
                score270Details.add(clock270ScoreDetail);
            }
        }
        int maxi = Stream.of(score90Details.stream(), score180Details.stream(), score270Details.stream())
                .flatMap(e -> e)
                .mapToInt(e -> e.score)
                .max()
                .orElse(-1);

        ScoreDetail maxiScoreDetail = score90Details.stream()
                    .filter(e -> e.score == maxi)
                    .findFirst()
                    .orElseGet(() -> score270Details.stream()
                                        .filter(e -> e.score == maxi)
                                        .findFirst()
                                        .orElseGet(() -> score180Details.stream()
                                                                .filter(e -> e.score == maxi)
                                                                .findFirst()
                                                                .orElse(null)
                            )
                    );

        List<int[]> arr = new ArrayList<>(maxiScoreDetail.pos);
        arr.sort((int[] a, int[] b) ->{
            if(a[1] == b[1])
                return b[0] - a[0];
            return a[1] - b[1];
        });

        int[][] nextBoard = maxiScoreDetail.nextBoard;
        while(arr.size() > 0){
            for(int[] e : arr){
                int val = task.pollFirst();
                maxiScoreDetail.nextBoard[e[0]][e[1]] = val;
            }
            score += arr.size();
            ScoreDetail scoreDetail = score(maxiScoreDetail.nextBoard);
            arr = new ArrayList<>(scoreDetail.pos);
            arr.sort((int[] a, int[] b) ->{
                if(a[1] == b[1])
                    return b[0] - a[0];
                return a[1] - b[1];
            });
            nextBoard = scoreDetail.nextBoard;
        }
        if(score != 0)
            System.out.print(String.valueOf(score) + ' ');
        dfs(level+1, nextBoard);
    }

    private void solve(){
        read();
        dfs(0, board);
    }

    public static void main(String[] args) {
        new Main().solve();
    }
}