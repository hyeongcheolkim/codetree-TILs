import java.util.*;
import java.util.stream.*;
import java.io.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static int[] rowDiff = new int[]{-1, 0, +1, 0};
    static int[] colDiff = new int[]{0, +1, 0, -1};
    static int counter = 1;

    public String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private int rowSize, colSize, k;
    private int[][] board;
    private boolean[][] isExit;

    private void initBoard(){
        board = new int[rowSize + 1][colSize + 1];
        for(int i=0;i<=rowSize;++i)
            for(int j=0;j<=colSize;++j)
                board[i][j] = -1;
        isExit = new boolean[rowSize+1][colSize+1];
    }

    private void printBoard(){
        for(int i=1;i<=rowSize;++i){
            for(int j=1;j<=colSize;++j){
                if(board[i][j] == -1)
                    System.out.print('X');
                else
                    System.out.print(board[i][j]);
                System.out.print(' ');
            }
            System.out.println();
        }
        System.out.println();
    }

    private void solve(){
        int answer = 0;
        int[] input = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::valueOf)
                            .toArray();
        rowSize = input[0];
        colSize = input[1];
        k = input[2];
        initBoard();

        while(k-- != 0){
            int[] data = Arrays.stream(readLine().split(" "))
                            .mapToInt(Integer::valueOf)
                            .toArray();
            int col = data[0];
            int direction = data[1];

            int[] prev = new int[]{-1, col, direction};
            int[] next = new int[]{-1, col, direction};
            do{
                prev = next;
                next = move(prev[0], prev[1], prev[2]);
            }while(!(prev[0] == next[0] && prev[1] == next[1]));

            do{
                prev = next;
                next = moveLeft(prev[0], prev[1], prev[2]);
            }while(!(prev[0] == next[0] && prev[1] == next[1]));

            do{
                prev = next;
                next = moveRight(prev[0], prev[1], prev[2]);
            }while(!(prev[0] == next[0] && prev[1] == next[1]));

            int nowRow = prev[0];
            int nowCol = prev[1];
            int nowDirection = prev[2];

            if(prev[0] <= 1){
                initBoard();
            }else{
                board[nowRow][nowCol] = counter;
                board[nowRow][nowCol + 1] = counter;
                board[nowRow][nowCol - 1] = counter;
                board[nowRow + 1][nowCol] = counter;
                board[nowRow - 1][nowCol] = counter;
                ++counter;

                isExit[nowRow + rowDiff[nowDirection]][nowCol + colDiff[nowDirection]] = true;
                answer += score(nowRow, nowCol);
            }
        }
        System.out.print(answer);
    }

    private int[] move(int initRow, int initCol, int initDirection){
        int row = initRow;
        int col = initCol;

        if(row == rowSize - 1)
            return new int[]{row, col, initDirection};

        int[] dr = {+1, +2, +1};
        int[] dc = {-1 , 0, +1};

        boolean moveable = true;
        for(int i=0; i<3; ++i){
            int nr = row + dr[i];
            int nc = col + dc[i];

            if(nr <= 0)     
                continue;
            if(!(1<=nr && nr<=rowSize && 1<=nc && nc<=colSize)){
                moveable = false;
                break;
            }
            if(board[nr][nc] != -1){
                moveable = false;
                break;
            }
        }
        if(moveable)
            return move(row + 1, col, initDirection);
        return new int[] {row, col, initDirection};
    }

    private int[] moveLeft(int initRow, int initCol, int initDirection){
        int row = initRow;
        int col = initCol;
        int direction = initDirection;

        if(row == rowSize - 1)
            return new int[]{row, col, initDirection};

        int[] dr = {-1,0,+1,+1,+2};
        int[] dc = {-1,-2,-2,-1,-1};

        boolean moveable = true;
        for(int i=0; i<5; ++i){
            int nr = row + dr[i];
            int nc = col + dc[i];

            if(nr <= 0)
                continue;

            if(!(1<=nr && nr<=rowSize && 1<=nc && nc<=colSize)){
                moveable = false;
                break;
            }
            if(board[nr][nc] != -1){
                moveable = false;
                break;
            }
        }
        if(moveable)
            return move(row + 1, col - 1, (direction - 1 == 0) ? 3 : direction - 1);
        return new int[] {row, col, direction};
    }

    private int[] moveRight(int initRow, int initCol, int initDirection){
        int row = initRow;
        int col = initCol;
        int direction = initDirection;

        if(row == rowSize - 1)
            return new int[]{row, col, initDirection};

        int[] dr = {-1,0,+1,+1,+2};
        int[] dc = {+1,+2,+2,+1,+1};

        boolean moveable = true;
        for(int i=0; i<5; ++i){
            int nr = row + dr[i];
            int nc = col + dc[i];

            if(nr <= 0)
                continue;

            if(!(1<=nr && nr<=rowSize && 1<=nc && nc<=colSize)){
                moveable = false;
                break;
            }
            if(board[nr][nc] != -1){
                moveable = false;
                break;
            }
        }
        if(moveable)
            return move(row + 1, col + 1, ++direction % 4);
        return new int[] {row, col, direction};
    }

    private int score(int row, int col){
        int ret = row;
        Queue<int[]> q = new LinkedList<>();

        boolean[][] vst = new boolean[rowSize + 1][colSize + 1];
        vst[row][col] = true;
        q.add(new int[]{row, col});

        while(!q.isEmpty()){
            int[] p = q.poll();
            int r = p[0];
            int c = p[1];
            int prev = board[r][c];

            for(int i=0;i<4;++i){
                int nr = r + rowDiff[i];
                int nc = c + colDiff[i];
                
                if(!(1<=nr && nr<=rowSize && 1<=nc && nc<=colSize))
                    continue;
                if(vst[nr][nc])
                    continue;
                if(board[nr][nc] < 0)
                    continue;
                if(prev != board[nr][nc] && !isExit[r][c])
                    continue;
                
                vst[nr][nc] = true;
                q.add(new int[]{nr, nc});
                ret = Integer.max(ret, nr);
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        new Main().solve();   
    }
}