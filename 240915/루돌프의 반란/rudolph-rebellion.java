import java.util.*;
import java.util.stream.*;
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

    int n, m, p, c, d;
    int Rr, Rc, exitCount=0;
    
    Map<Integer, int[]> santaLocations = new HashMap<>();
    Map<Integer, Integer> stun = new HashMap<>();
    Map<Integer, Integer> score = new HashMap<>();
    int[][] board;

    int distance(int[] a, int[] b){
        return (a[0] - b[0])*(a[0] - b[0]) + (a[1] - b[1])*(a[1] - b[1]);
    }

    void printBoard(){
        System.out.println(String.format("Rr:%d Rc%d",Rr,Rc));
        for(int i=1;i<=n;++i){
            for(int j=1;j<=n;++j){
                System.out.print(board[i][j] == -1 ? "X" : String.valueOf(board[i][j]));
                System.out.print(' ');
            }
            System.out.println();
        }
        for(int i=1;i<=p;++i){
            System.out.println(String.format("santa:%d score:%d %s",i,score.get(i),Arrays.toString(santaLocations.get(i))));
        }
        System.out.println();
    }

    int rudolphMoveTarget(){
        int[] rodolphLocation = new int[]{Rr, Rc};
        Map.Entry<Integer, int[]> targetEntry = santaLocations.entrySet()
                    .stream()
                    .filter(x -> x.getValue()[0] != -1 && x.getValue()[1] != -1)
                    .sorted((Map.Entry<Integer, int[]> a, Map.Entry<Integer, int[]> b) -> {
                        int aDistance = distance(rodolphLocation, a.getValue());
                        int bDistance = distance(rodolphLocation, b.getValue());
                        if(aDistance == bDistance){
                            if(a.getValue()[0] == b.getValue()[0])
                                return b.getValue()[1] - a.getValue()[1];
                            return b.getValue()[0] - a.getValue()[0];
                        }
                        return aDistance - bDistance;
                    })
                    .collect(Collectors.toList())
                    .get(0);
        return targetEntry.getKey();
    }

    int[] rudolphMoveLocation(int Sn){
        int[] target = santaLocations.get(Sn);
        int[] dr = new int[]{-1,-1,-1,0,0,+1,+1,+1};
        int[] dc = new int[]{-1,0,+1,-1,+1,-1,0,+1};

        int minDistance = Integer.MAX_VALUE;
        int rDiff = 0;
        int cDiff = 0;
        for(int direction=0;direction<8;++direction){
            int nr = Rr + dr[direction];
            int nc = Rc + dc[direction];

            if(!(1<=nr && nr<=n && 1<=nc && nc<=n))
                continue;
            int dist = distance(new int[]{nr, nc}, new int[]{target[0], target[1]});
            if(dist < minDistance){
                minDistance = dist;
                rDiff = dr[direction];
                cDiff = dc[direction];
            }
        }
        if(minDistance != Integer.MAX_VALUE){
            return new int[]{rDiff, cDiff};
        }
        return new int[]{0,0};
    }

    int[] santaMoveLocation(int Sn){
        int[] nowLocation = santaLocations.get(Sn);
        int[] dr = new int[]{-1,0,+1,0};
        int[] dc = new int[]{0,+1,0,-1};
        int initDistance = distance(new int[]{Rr, Rc}, nowLocation);
        int minDistance = initDistance;
        int rDiff = 0;
        int cDiff = 0;
        for(int direction=0;direction<4;++direction){
            int nr = nowLocation[0] + dr[direction];
            int nc = nowLocation[1] + dc[direction];

            if(!(1<=nr && nr<=n && 1<=nc && nc<=n))
                continue;
            if(board[nr][nc] >= 1)
                continue;
            int dist = distance(new int[]{nr, nc}, new int[]{Rr, Rc});
            // System.out.println(String.format("dist=%d dr=%d dc=%d", dist, dr[direction], dc[direction]));
            if(dist < minDistance){
                minDistance = dist;
                rDiff = dr[direction];
                cDiff = dc[direction];
            }
        }
        if(minDistance != initDistance){
            return new int[]{rDiff, cDiff};
        }
        return new int[]{0,0};
    }

    void rudolphToSanta(int Sn, int dr, int dc, int turn){
        score.merge(Sn, c, Integer::sum);

        int[] loc = santaLocations.get(Sn);
        int nr = loc[0] + dr * c;
        int nc = loc[1] + dc * c;

        if(!(1<=nr && nr<=n && 1<=nc && nc<=n)){
            santaLocations.put(Sn, new int[]{-1,-1});
            ++exitCount;
            return;
        }

        if(board[nr][nc] >= 1)
            santaMoveCascade(board[nr][nc], dr, dc);
        board[nr][nc] = Sn;
        santaLocations.put(Sn, new int[]{nr, nc});

        stun.put(Sn, turn + 1);
    }

    void santaToRudolph(int Sn, int dr, int dc, int turn){
        score.merge(Sn, d, Integer::sum);

        int[] loc = santaLocations.get(Sn);
        int nr = loc[0] + ((-dr) * (d-1));
        int nc = loc[1] + ((-dc) * (d-1));
        // System.out.println(String.format("Sn:%d nowR:%d nowC:%d dr:%d dc:%d nr:%d nc:%d turn:%d", Sn, loc[0], loc[1], dr, dc,nr, nc, turn));
        if(!(1<=nr && nr<=n && 1<=nc && nc<=n)){
            santaLocations.put(Sn, new int[]{-1,-1});
            ++exitCount;
            return;
        }

        if(board[nr][nc] >= 1)
            santaMoveCascade(board[nr][nc], -dr, -dc);
        board[nr][nc] = Sn;
        santaLocations.put(Sn, new int[]{nr, nc});
        stun.put(Sn, turn + 1);
    }

    void santaMoveCascade(int Sn, int dr, int dc){
        int[] nowLoc = santaLocations.get(Sn);

        int nr = nowLoc[0] + dr;
        int nc = nowLoc[1] + dc;

        if(!(1<=nr && nr<=n && 1<=nc && nc<=n)){
            santaLocations.put(Sn, new int[]{-1,-1});
            ++exitCount;
            return;
        }

        if(board[nr][nc] >= 1)
            santaMoveCascade(board[nr][nc], dr, dc);

        board[nr][nc] = Sn;
        santaLocations.put(Sn, new int[]{nr, nc});
    }

    void solve(){
        int[] metaData = Arrays.stream(readLine().split(" "))
                                .mapToInt(Integer::parseInt)
                                .toArray();
        n = metaData[0];
        m = metaData[1];
        p = metaData[2];
        c = metaData[3];
        d = metaData[4];
        
        board = new int[n+1][n+1];

        int[] rudolphLocation = Arrays.stream(readLine().split(" "))
                                .mapToInt(Integer::parseInt)
                                .toArray();
        Rr = rudolphLocation[0];
        Rc = rudolphLocation[1];
        board[Rr][Rc] = -1;

        for(int i=0;i<p;++i){
            int[] santaMeta = Arrays.stream(readLine().split(" "))
                                    .mapToInt(Integer::parseInt)
                                    .toArray();
            int Sn = santaMeta[0];
            int Sr = santaMeta[1];
            int Sc = santaMeta[2];

            board[Sr][Sc] = Sn;
            santaLocations.put(Sn, new int[]{Sr, Sc});
            stun.put(Sn, -1);
        }

        for(int turn=1;turn<=m;++turn){
            // System.out.println("turn:" + String.valueOf(turn));
            // printBoard();

            int targetSn = rudolphMoveTarget();
            int[] diff = rudolphMoveLocation(targetSn);
            board[Rr][Rc] = 0;
            Rr += diff[0];
            Rc += diff[1];
            if(board[Rr][Rc] >= 1)
                rudolphToSanta(targetSn, diff[0], diff[1], turn);
            board[Rr][Rc] = -1;

            for(int i=1;i<=p;++i){
                if(turn <= stun.get(i)){
                    // System.out.println("Santa stun" + String.valueOf(i));
                    continue;
                }
                int[] nowLoc = santaLocations.get(i);
                if(nowLoc[0] == -1 && nowLoc[1] == -1){
                    // System.out.println("Santa exit" + String.valueOf(i));
                    continue;
                }
                int[] santaDiff = santaMoveLocation(i);

                int nr = nowLoc[0] + santaDiff[0];
                int nc = nowLoc[1] + santaDiff[1];
                board[nowLoc[0]][nowLoc[1]] = 0;

                // System.out.println(String.format("turn:%d Sn:%d nowR:%d nowC:%d nr:%d nc:%d", turn, i, nowLoc[0], nowLoc[1], nr, nc));
                if(board[nr][nc] == -1){
                    santaToRudolph(i, santaDiff[0], santaDiff[1], turn);
                }else{
                    board[nr][nc] = i;
                    santaLocations.put(i, new int[]{nr, nc});
                }
            }
            if(exitCount == p){
                break;
            }

            for(int i=1;i<=p;++i){
                int[] loc = santaLocations.get(i);
                if(loc[0] != -1 && loc[1] != -1)
                score.merge(i, +1, Integer::sum);
            }
        }

        for(int i=1;i<=p;++i){
            System.out.print(score.getOrDefault(i, 0));
            System.out.print(' ');
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solve();
    }
}