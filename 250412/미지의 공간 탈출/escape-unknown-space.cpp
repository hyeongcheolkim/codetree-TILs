#include <iostream>
#include <vector>
#include <algorithm>
#include <deque>
#include <queue>
#include <unordered_map>
using namespace std;

int n, m, f, SIZE;
vector<vector<int>> base, flat;
pair<int,int> dot1, dot2;
const pair<int,int> OUT_OF_BOUND = pair<int,int>{-5, -5};

unordered_map<int, int> error_direction;
unordered_map<int, vector<int>> error_turn;

const int dr[]{0,0,+1,-1}, dc[]{+1,-1,0,0};

int a1, a2, b1, b2;

bool isOutOfBound(int row, int col, int size = SIZE)
{
    return !(0<=row && row<size && 0<=col && col<size);
}

bool isT1(int row, int col)
{
    if(isOutOfBound(row, col, SIZE))
        return false;
    if(0<=row && row<a1 || a1+3*m<=row && row<a1+3*m+a2)
    {
        if(b1<=col && col<b1+m)
            return true;
        if(b1+2*m<=col && col<b1+3*m)
            return true;
    }
    if(a1<=row && row<a1+m || a1+2*m<=row && row<a1+3*m)
    {
        if(0<=col && col<b1)
            return true;
        if(b1+3*m<=col && col<b1+3*m+b2)
            return true;
    }
    return false;
}

bool isT2(int row, int col)
{   
    if(isOutOfBound(row, col, SIZE))
        return false;
    if(a1<=row && row<a1+m)
    {
        if(b1<=col && col<b1+m)
            return true;
        if(b1+2*m<=col && col<b1+3*m)
            return true;
    }
    if(a1+2*m<=row && row<a1+3*m)
    {
        if(b1<=col && col<b1+m)
            return true;
        if(b1+2*m<=col && col<b1+3*m)
            return true;
    }
    return false;
}

void printVec(vector<vector<int>> target)
{
    cout << "[vec]" << '\n';
    for(int i=0;i<target.size();++i)
    {
        for(int j=0;j<target[0].size();++j)
        {
            cout << target[i][j] << ' ';
        }
        cout << '\n';
    }
}

void printSpace(vector<vector<int>> target)
{
    cout << "[vec]" << '\n';
    for(int i=0;i<target.size();++i)
    {
        for(int j=0;j<target[0].size();++j)
        {

            if(isT2(i,j))
                cout << 'k' << ' ';
            else if(isT1(i,j))
                cout << 'v' << ' ';
            else
                cout << target[i][j] << ' ';
        }
        cout << '\n';
    }
}

vector<vector<int>> rotate_2nd(const vector<vector<int>>& target)
{
    int row_size = target.size();
    int col_size = target[0].size();
    vector<vector<int>> ret(col_size, vector<int>(row_size, 0));

    for(int i=0;i<row_size;++i)
        for(int j=0;j<col_size;++j)
        {
            ret[j][row_size - 1 - i] = target[i][j];
        }
    return ret;
}

vector<vector<int>> inputWall(int size)
{
    vector<vector<int>> ret(size, vector<int>(size));
    for(int i=0;i<size;++i)
        for(int j=0;j<size;++j)
            cin >> ret[i][j];
    return ret;
}

pair<int,int> getPos(int i, int j)
{
     if(0<=i && i<a1)
    {
        if(0<=j && j<b1)
        {
            return {i,j};
        }
        if(b1<=j && j<b1+m)
        {
            return {i, j+m};
        }
        if(b1+m<=j && j<b1+m+b2)
        {
            return{i,j+2*m};
        }
    }
    if(a1<=i && i<a1+m)
    {
        if(0<=j && j<b1)
        {
            return {i+m,j};
        }
        if(b1+m<=j && j<b1+m+b2)
        {
            return {i+m, j+2*m};
        }
    }
    if(a1+m<=i && i<a1+m+a2)
    {
        if(0<=j && j<b1)
        {
            return {i+2*m,j};
        }
        if(b1<=j && j<b1+m)
        {
            return {i+2*m, j+m};
        }
        if(b1+m<=j && j<b1+m+b2)
        {
            return {i+2*m,j+2*m};
        }
    }
    return OUT_OF_BOUND;
}

pair<int,int> nextPos(int row, int col, int d)
{
    if(isOutOfBound(row, col, SIZE))
        return OUT_OF_BOUND;
    int nr = row + dr[d];
    int nc = col + dc[d];

    if(isOutOfBound(nr, nc, SIZE))
        return OUT_OF_BOUND;
    if(isT1(nr,nc))
    {
        while(isT1(nr,nc))
        {
            nr += dr[d];
            nc += dc[d];
        }
        if(isOutOfBound(nr, nc))
            return OUT_OF_BOUND;
        return {nr, nc};
    }
    if(isT2(nr, nc))
    {
        if(d == 0 || d == 1)
        {
            for(int diff=1;diff<=m;++diff)
            {
                int r1 = nr - diff;
                int c1 = nc;
                int r2 = nr + diff;;
                int c2 = nc;
                if(!isOutOfBound(r1, c1, SIZE) && !isT2(r1,c1) && !isT1(r1, c1))
                    return {r1, c1};
                if(!isOutOfBound(r2, c2, SIZE) && !isT2(r2,c2) && !isT1(r2, c2))
                    return {r2, c2};
                nr += dr[d];
                nc += dc[d];
            }
            return OUT_OF_BOUND;
        }
        if(d == 2 || d == 3)
        {
            for(int diff=1;diff<=m;++diff)
            {
                int r1 = nr;
                int c1 = nc - diff;;
                int r2 = nr;
                int c2 = nc + diff;;
                if(!isOutOfBound(r1, c1, SIZE) &&!isT2(r1,c1) && !isT1(r1, c1))
                    return {r1, c1};
                if(!isOutOfBound(r2, c2, SIZE) &&!isT2(r2,c2) && !isT1(r2, c2))
                    return {r2, c2};
                nr += dr[d];
                nc += dc[d];
            }
            return OUT_OF_BOUND;
        }
    }
    return {nr, nc};
}

int escape()
{
    int sr, sc, er, ec;
    for(int i=0;i<SIZE;++i)
        for(int j=0;j<SIZE;++j)
        {
            if(flat[i][j] == 2)
            {
                sr = i;
                sc = j;
            }
            if(flat[i][j] == 4)
            {
                er = i;
                ec = j;
            }
        }

    vector<vector<bool>> vst(SIZE, vector<bool>(SIZE));
    queue<tuple<int,int, int>> q;
    q.emplace(sr, sc, 0);
    int turn = 1;
    while(!q.empty())
    {
        for(const auto& et : error_turn[turn])
        {
            int d = error_direction[et];
            vector<pair<int,int>> target;
            for(int i=0;i<SIZE;++i)
                for(int j=0;j<SIZE;++j)
                {
                    if(flat[i][j] == et)
                    {
                        auto ret = nextPos(i, j, d);
                        if(ret == OUT_OF_BOUND)
                            continue;
                        auto[nr, nc] = ret;
                        if(flat[nr][nc] == 1)
                            continue;
                        if(flat[nr][nc] == 4)
                            continue;
                        target.push_back(ret);
                    }
                }
            for(const auto&[r,c] : target)
                flat[r][c]  = et;
        }   

        decltype(q) tmpq;
        while(!q.empty())
        {
            auto [r, c, t] = q.front();
            q.pop();
            for(int direction=0;direction<4;++direction)
            {
                auto ret = nextPos(r, c, direction);
                if(ret == OUT_OF_BOUND)
                    continue;
                auto[nr, nc] = ret;
                if(flat[nr][nc] == 1)
                    continue;
                if(vst[nr][nc])
                    continue;
                if(flat[nr][nc] < 0)
                    continue;
                if(nr == er && nc == ec)
                {
                    return t + 1;
                }
                vst[nr][nc] = true;
                tmpq.emplace(nr, nc, t + 1);
            }   
        }
        q.swap(tmpq);
        ++turn;
    }
    return -1;
}

int main()
{
    cin >> n >> m >> f;
    SIZE = n + 2*m;
    base.resize(n, vector<int>(n, 0));
    deque<pair<int,int>> tmp_dq;
    for(int i=0;i<n;++i)
        for(int j=0;j<n;++j)
        {
            cin >> base[i][j];
            if(base[i][j] == 3)
                tmp_dq.emplace_back(i, j);
        }
    dot1 = tmp_dq.front();
    dot2 = tmp_dq.back();

    a1 = dot1.first;
    a2 = (n - 1) - dot2.first;
    b1 = dot1.second;
    b2 = (n - 1) - dot2.second;

    flat.resize(SIZE, vector<int>(SIZE, 0));
    for(int i=0;i<n;++i)
    {
        for(int j=0;j<n;++j)
        {
            auto ret = getPos(i,j);
            auto[row, col] = ret;
            if(ret != OUT_OF_BOUND)
                flat[row][col] = base[i][j];
        }
    }
    auto ew = rotate_2nd(rotate_2nd(rotate_2nd(inputWall(m))));
    auto ww = rotate_2nd(inputWall(m));
    auto nw = inputWall(m);
    auto sw = rotate_2nd(rotate_2nd(inputWall(m)));
    auto top = inputWall(m);

    for(int i=0;i<m;++i)
        for(int j=0;j<m;++j)
            flat[i+a1+m][j+b1+2*m] = ew[i][j];

    for(int i=0;i<m;++i)
        for(int j=0;j<m;++j)
            flat[i+a1+m][j+b1] = ww[i][j];

    for(int i=0;i<m;++i)
        for(int j=0;j<m;++j)
            flat[i+a1+2*m][j+b1+m] = nw[i][j];

    for(int i=0;i<m;++i)
        for(int j=0;j<m;++j)
            flat[i+a1][j+b1+m] = sw[i][j];

    for(int i=0;i<m;++i)
        for(int j=0;j<m;++j)
            flat[i+a1+m][j+b1+m] = top[i][j];

    for(int i=0;i<f;++i)
    {
        int r, c, d, v;
        int idx = -(i+1);
        cin >> r >> c >> d >> v;
        auto[row,col] = getPos(r, c);
        flat[row][col] = idx;

        error_direction[idx] = d;
        error_turn[v].push_back(idx);
    }
    // printSpace(flat);
    cout << escape();

    
    // auto[row, col] = nextPos(7, 2, 2);
    return 0;
}
