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

    int N, M, K;
    int[][] maze;
    int answer = 0;
    int exitCnt;
    int[] exit;

    static int[] dr = {+1,-1,0,0};
    static int[] dc = {0,0,+1,-1};

    int calculateDistance(int[] d1, int[] d2){
        return Math.abs(d1[0] - d2[0]) + Math.abs(d1[1] - d2[1]);
    }

    void move(){
        int cnt = 0;
        int[][] nextMaze = new int[N+1][N+1];
        for(int i=1;i<=N;++i)
            for(int j=1;j<=N;++j){
                nextMaze[i][j] = maze[i][j];
                if(maze[i][j] == 100)
                    exit = new int[]{i,j};
            }

        for(int r=1;r<=N;++r)
            for(int c=1;c<=N;++c){
                if(maze[r][c] >= 0)
                    continue;
                for(int i=0;i<-maze[r][c];++i){
                    int targetDist = calculateDistance(new int[]{r, c}, exit);

                    for(int direction = 0;direction<4;++direction){
                        int nr = r + dr[direction];
                        int nc = c + dc[direction];

                        if(!(1<=nr && nr<=N && 1<=nc && nc<=N))
                            continue;
                        if(0 < maze[nr][nc] && maze[nr][nc] < 10)
                            continue;
                        
                        int dist = calculateDistance(new int[]{nr,nc}, exit);

                        if(dist < targetDist){
                            targetDist = dist;
                            ++nextMaze[r][c];
                            if(maze[nr][nc] <= 0)
                                --nextMaze[nr][nc];
                            if(maze[nr][nc] == 100)
                                --exitCnt;
                            ++cnt;
                        }
                    }
                }
            }
        
        for(int i=1;i<=N;++i)
            for(int j=1;j<=N;++j)
                maze[i][j] = nextMaze[i][j];

        answer += cnt;
    }

    void rotateSquare(int row, int col, int size){
        int[][] newSquare = new int[size][size];

        for(int i=0;i<size;++i)
            for(int j=0;j<size;++j){
                newSquare[i][j] = maze[row + size - 1 - j][col + i];
            }

        for(int i=0;i<size;++i)
            for(int j=0;j<size;++j){
                maze[row+i][col+j] = newSquare[i][j];
                if(0<maze[row+i][col+j] && maze[row+i][col+j]<10){
                    --maze[row+i][col+j];
                }
                if(maze[row+i][col+j] == 100)
                    exit = new int[]{row+i,col+j};
            }
    }

    boolean containsHuman(int row, int col, int size){
        for(int i=row;i<row+size;++i)
            for(int j=col;j<col+size;++j)
                if(maze[i][j] < 0)
                    return true;
        return false;
    }

    void rotateMaze(){
        int size = 2;
        
        while(N - size >= 0){
            for(int i=size-1;i>=0;--i){
                for(int j=size-1;j>=0;--j){
                    int row = exit[0] - i;
                    int col = exit[1] - j;

                    if(!(1<= row && row <= N - size + 1 && 1<=col && col <= N - size + 1))
                        continue;

                    if(containsHuman(row, col, size)){
                        rotateSquare(row, col, size);
                        return;
                    }
                }
            }
            ++size;
        }
        return;
    }

    void init(){
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
        N = input[0];
        M = input[1];
        K = input[2];
        exitCnt = M;

        maze = new int[N+1][N+1];

        for(int i=0;i<N;++i){
            int[] line = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .limit(N)
                            .toArray();

            for(int j=0;j<N;++j){
                maze[i+1][j+1] = line[j];
            }
        }

        for(int i=0;i<M;++i){
            int[] line = Arrays.stream(readLine().split(" "))
                .mapToInt(Integer::parseInt)
                .limit(2)
                .toArray();
            --maze[line[0]][line[1]];
        }
        exit = Arrays.stream(readLine().split(" "))
                .mapToInt(Integer::parseInt)
                .limit(2)
                .toArray();

        maze[exit[0]][exit[1]] = 100;
    }

    void printMaze(){
        for(int i=1;i<=N;++i){
            for(int j=1;j<=N;++j)
                System.out.print(maze[i][j] + " ");
            System.out.println();
        }
        System.out.println();
    }

    void solve(){
        // printMaze();
        while(K-- != 0){
            move();
            // System.out.println(answer);
            rotateMaze();
            // printMaze();
            if(exitCnt <= 0){
                break;
            }
        }


        System.out.println(answer);
        System.out.print(String.format("%d %d", exit[0], exit[1]));
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.init();
        main.solve();
    }
}