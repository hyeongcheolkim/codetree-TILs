#include <iostream>
#include <algorithm>
#include <limits>
#include <vector>
#include <queue>
#include <utility>
using namespace std;

int n, m, sr, sc, er, ec;
int eye = 0;
const int INF = numeric_limits<int>::max() / 10;
vector<vector<int>> board;
vector<vector<int>> warriors;
vector<vector<int>> distances;

void calculateDistance()
{
    const int dr[]{-1, +1, 0, 0}, dc[]{0, 0, -1, +1};
    queue<pair<int, int>> q;

    q.emplace(er, ec);
    distances[er][ec] = 0;

    while (!q.empty())
    {
        const auto [r, c] = q.front();
        q.pop();
        const int now_vst_count = distances[r][c];
        for (int direction = 0; direction < 4; ++direction)
        {
            int nr = r + dr[direction];
            int nc = c + dc[direction];

            if (!(0 <= nr && nr < n && 0 <= nc && nc < n))
                continue;
            if (board[nr][nc] == 1)
                continue;
            if (distances[nr][nc] <= now_vst_count + 1)
                continue;
            distances[nr][nc] = now_vst_count + 1;
            q.emplace(nr, nc);
        }
    }
}

void make_safe_line(vector<vector<int>> &target, int row, int col, int d)
{
    const int dr[]{-1, 0, +1, 0}, dc[]{0, +1, 0, -1};
    while (0 <= row && row < n && 0 <= col && col < n)
    {
        target[row][col] = -1;
        row += dr[d];
        col += dc[d];
    }
}

int line_process(vector<vector<int>> &target, int row, int col, int d)
{
    const int dr[]{-1, 0, +1, 0}, dc[]{0, +1, 0, -1};
    int ret = 0;
    while (true)
    {
        if (!(0 <= row && row < n && 0 <= col && col < n))
            break;
        if (target[row][col] > 0)
        {
            ret = target[row][col];
            target[row][col] = -INF;
        }
        row += dr[d];
        col += dc[d];
        if (ret != 0)
        {
            make_safe_line(target, row, col, d);
            break;
        }
    }
    return ret;
}

void make_safe_vertical(vector<vector<int>> &target, int row, int col, int d, int vertical_dr, int vertical_dc)
{
    const int dr[]{-1, 0, +1, 0}, dc[]{0, +1, 0, -1};
    make_safe_line(target, row + dr[d], col + dc[d], d);

    while (true)
    {
        row += vertical_dr;
        col += vertical_dc;
        if (!(0 <= row && row < n && 0 <= col && col < n))
            break;
        make_safe_line(target, row, col, d);
    }
}

int vertical_line_process(vector<vector<int>> &target, int row, int col, int d, int vertical_dr, int vertical_dc)
{
    const int dr[]{-1, 0, +1, 0}, dc[]{0, +1, 0, -1};
    int ret = 0;
    while (true)
    {
        if (!(0 <= row && row < n && 0 <= col && col < n))
            break;
        if (target[row][col] > 0)
        {
            ret = target[row][col];
            target[row][col] = -INF;
        }
        if (ret != 0)
        {
            make_safe_vertical(target, row, col, d, vertical_dr, vertical_dc);
            break;
        }
        row += dr[d];
        col += dc[d];
    }
    return ret;
}

void line_make_visible(vector<vector<bool>> &target, int row, int col, int d)
{
    const int dr[]{-1, 0, +1, 0}, dc[]{0, +1, 0, -1};
    while ((0 <= row && row < n && 0 <= col && col < n))
    {
        target[row][col] = true;
        row += dr[d];
        col += dc[d];
    }
}

vector<vector<bool>> getVisibleArea(int d)
{
    vector<vector<bool>> ret(n, vector<bool>(n, false));
    const int drs[]{-1, -1, 0, +1, +1, +1, 0, -1}, dcs[]{0, +1, +1, +1, 0, -1, -1, -1};
    int direction = 2 * d;
    int dr = drs[direction];
    int dc = dcs[direction];

    int dr1 = drs[(direction + 1) % 8];
    int dc1 = dcs[(direction + 1) % 8];

    int dr2 = drs[((direction - 1) + 8) % 8];
    int dc2 = dcs[((direction - 1) + 8) % 8];

    int r1 = sr, r2 = sr;
    int c1 = sc, c2 = sc;

    line_make_visible(ret, r1 + dr, c1 + dc, d);
    while (true)
    {
        if (!(0 <= r1 && r1 < n && 0 <= c1 && c1 < n))
            break;
        r1 += dr1;
        c1 += dc1;
        line_make_visible(ret, r1, c1, d);
    }
    while (true)
    {
        if (!(0 <= r2 && r2 < n && 0 <= c2 && c2 < n))
            break;
        r2 += dr2;
        c2 += dc2;
        line_make_visible(ret, r2, c2, d);
    }
    return ret;
}

pair<int, vector<vector<int>>> eye_beam(vector<vector<int>> target, int mr, int mc, int d)
{
    const int drs[]{-1, -1, 0, +1, +1, +1, 0, -1}, dcs[]{0, +1, +1, +1, 0, -1, -1, -1};
    int ret = 0;
    int direction = 2 * d;
    int dr = drs[direction];
    int dc = dcs[direction];
    ret += line_process(target, mr, mc, d);

    int dr1 = drs[(direction + 1) % 8];
    int dc1 = dcs[(direction + 1) % 8];

    int dr2 = drs[((direction - 1) + 8) % 8];
    int dc2 = dcs[((direction - 1) + 8) % 8];

    int r1 = mr, c1 = mc, r2 = mr, c2 = mc;

    while (true)
    {
        if (!(0 <= r1 && r1 < n && 0 <= c1 && c1 < n))
            break;
        r1 += dr1;
        c1 += dc1;
        ret += vertical_line_process(target, r1, c1, d, dr1, dc1);
    }

    while (true)
    {
        if (!(0 <= r2 && r2 < n && 0 <= c2 && c2 < n))
            break;
        r2 += dr2;
        c2 += dc2;
        ret += vertical_line_process(target, r2, c2, d, dr2, dc2);
    }
    return {ret, target};
}

void printBoard()
{
    cout << "[board]" << '\n';
    for (int i = 0; i < n; ++i)
    {
        for (int j = 0; j < n; ++j)
            cout << board[i][j] << ' ';
        cout << '\n';
    }
}

void printWarriors()
{
    cout << "[warrior]" << '\n';
    for (int i = 0; i < n; ++i)
    {
        for (int j = 0; j < n; ++j)
        {
            if (i == sr && j == sc)
                cout << 'x' << ' ';
            else
                cout << warriors[i][j] << ' ';
        }
        cout << '\n';
    }
}

void printVec(vector<vector<int>> vec)
{
    cout << "[vec]" << '\n';
    for (int i = 0; i < n; ++i)
    {
        for (int j = 0; j < n; ++j)
        {
            if (vec[i][j] == -INF)
                cout << 's' << ' ';
            else
                cout << vec[i][j] << ' ';
        }
        cout << '\n';
    }
}

void printVec(vector<vector<bool>> &vec)
{
    cout << "[vec]" << '\n';
    for (int i = 0; i < n; ++i)
    {
        for (int j = 0; j < n; ++j)
            cout << vec[i][j] << ' ';
        cout << '\n';
    }
}

pair<int, vector<vector<int>>> move_warriors(
    vector<vector<int>> &target,
    vector<vector<int>> &is_stoned,
    vector<vector<bool>> &visible_area,
    vector<vector<int>> &safe_zone)
{
    vector<vector<int>> ret1(n, vector<int>(n, 0));
    vector<vector<int>> ret2(n, vector<int>(n, 0));

    const int dr1[]{+1, -1, 0, 0, 0}, dc1[]{0, 0, -1, +1, 0};
    const int dr2[]{0, 0, +1, -1, 0}, dc2[]{-1, +1, 0, 0, 0};
    int cnt = 0;

    for (int i = 0; i < n; ++i)
        for (int j = 0; j < n; ++j)
        {
            int ea = warriors[i][j] - is_stoned[i][j];
            if (ea <= 0)
                continue;

            int dist = INF;
            int d = -1;
            for (int direction = 0; direction < 5; ++direction)
            {
                int nr = i + dr1[direction];
                int nc = j + dc1[direction];

                if (!(0 <= nr && nr < n && 0 <= nc && nc < n))
                    continue;
                if (visible_area[nr][nc] && (safe_zone[nr][nc] != -1))
                    continue;
                if (is_stoned[nr][nc])
                    continue;
                int diff = abs(nr - sr) + abs(nc - sc);

                if (diff < dist)
                {
                    dist = diff;
                    d = direction;
                }
            }
            ret1[i + dr1[d]][j + dc1[d]] += ea;
            if (d < 4)
                cnt += ea;
        }
    for (int i = 0; i < n; ++i)
        for (int j = 0; j < n; ++j)
        {
            int ea = ret1[i][j] - is_stoned[i][j];
            if (ea <= 0)
                continue;

            int dist = INF;
            int d = -1;
            for (int direction = 0; direction < 5; ++direction)
            {
                int nr = i + dr2[direction];
                int nc = j + dc2[direction];

                if (!(0 <= nr && nr < n && 0 <= nc && nc < n))
                    continue;
                if (visible_area[nr][nc] && (safe_zone[nr][nc] != -1))
                    continue;
                if (is_stoned[nr][nc])
                    continue;
                int diff = abs(nr - sr) + abs(nc - sc);

                if (diff < dist)
                {
                    dist = diff;
                    d = direction;
                }
            }
            ret2[i + dr2[d]][j + dc2[d]] += ea;
            if (d < 4)
                cnt += ea;
        }
    return {cnt, ret2};
}

int main()
{
    cin >> n >> m;
    cin >> sr >> sc >> er >> ec;

    board.resize(n, vector<int>(n));
    warriors.resize(n, vector<int>(n, 0));
    for (int i = 0; i < m; ++i)
    {
        int r, c;
        cin >> r >> c;
        ++warriors[r][c];
    }

    for (int i = 0; i < n; ++i)
        for (int j = 0; j < n; ++j)
            cin >> board[i][j];

    distances.resize(n, vector<int>(n, INF));
    calculateDistance();

    if(distances[sr][sc] == INF)
    {
        cout << -1;
        return 0;
    }

    while (true)
    {
        const int dr[]{-1,+1,0,0}, dc[]{0,0,-1,+1};
        for(int direction =0;direction<4;++direction)
        {
            int nr = sr + dr[direction];
            int nc = sc + dc[direction];
            if (!(0 <= nr && nr < n && 0 <= nc && nc < n))
                    continue;
            if(distances[nr][nc] < distances[sr][sc])
            {
                sr = nr;
                sc = nc;
                break;
            }
        }
        if(sr == er  && sc == ec)
            break;
        warriors[sr][sc] = 0;
        int stoned = 0;
        // printWarriors();
        vector<vector<int>> tmp = warriors;
        for (const int direction : vector<int>{0, 2, 3, 1})
        {
            auto [cnt, v] = eye_beam(warriors, sr, sc, direction);
            if (stoned < cnt)
            {
                eye = direction;
                stoned = cnt;
                tmp = v;
            }
        }
        // printVec(tmp);
        vector<vector<int>> is_stoned(n, vector<int>(n, 0));
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
            {
                if (tmp[i][j] == -INF)
                    is_stoned[i][j] += warriors[i][j];
            }

        auto area = getVisibleArea(eye);
        // printVec(area);
        auto [warriors_move_cnt, next_warriors] = move_warriors(warriors, is_stoned, area, tmp);
        vector<vector<int>> new_warriors(n, vector<int>(n, 0));
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
            {
                new_warriors[i][j] += next_warriors[i][j];
                new_warriors[i][j] += is_stoned[i][j];
            }
        warriors = new_warriors;
        // printWarriors();

        cout << warriors_move_cnt << ' ' << stoned << ' ' << next_warriors[sr][sc] << '\n';
        warriors[sr][sc] = 0;
    }
    cout << 0;
    return 0;
}