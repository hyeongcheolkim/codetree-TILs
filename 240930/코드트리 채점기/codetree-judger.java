import java.util.Scanner;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.PriorityQueue;

// 링크의 정보를 나타내는 클래스 선언
class Url implements Comparable<Url> {
    int tme, id;
    int num;

    public Url(int tme, int id, int num) {
        this.tme = tme;
        this.id = id;
        this.num = num;
    }

    // 우선순위가 높은 url을 결정하기 위해 정렬함수를 만들어줍니다.
    @Override
    public int compareTo(Url url) {
        if(this.id != url.id) return this.id - url.id;
        return this.tme - url.tme;
    }
};

public class Main {
    public static final int MAX_D = 300;
    public static final int MAX_N = 50000;
    public static final int INF = 1987654321;
    
    public static int q;
    public static int n;
    
    // 해당 도메인에서 해당 문제ID가 레디큐에 있는지 관리해줍니다.
    public static TreeSet[] isInReadyq = new TreeSet[MAX_D + 1];
    
    // 현재 쉬고 있는 채점기들을 관리해줍니다.
    public static PriorityQueue<Integer> restJudger = new PriorityQueue<>();
    
    // 각 채점기들이 채점할 때, 도메인의 인덱스를 저장합니다.
    public static int[] judgingDomain = new int[MAX_N + 1];
    
    // 각 도메인별 start, gap, end(채점이 가능한 최소 시간)을 관리합니다.
    public static int[] s = new int[MAX_D + 1];
    public static int[] g = new int[MAX_D + 1];
    public static int[] e = new int[MAX_D + 1];
    
    // 도메인을 관리하기 위해 cnt를 이용합니다.
    // 도메인 문자열을 int로 변환해주는 map을 관리합니다.
    public static TreeMap<String, Integer> domainToIdx = new TreeMap<>();
    public static int cnt;
    
    // 현재 채점 대기 큐에 있는 task의 개수를 관리합니다.
    public static int ans;
    
    // 각 도메인별로 priority queue를 만들어
    // 우선순위가 가장 높은 url을 뽑아줍니다.
    public static PriorityQueue<Url>[] urlPq = new PriorityQueue[MAX_D + 1];
    
    // 채점기를 준비합니다.
    public static void init(Scanner sc) {
        String url;
        n = sc.nextInt();
        url = sc.next();
    
        for(int i = 1; i <= n; i++) restJudger.add(i);
    
        // url에서 도메인 부분과 숫자 부분을 나누어줍니다.
        int idx = 0;
        for(int i = 0; i < url.length(); i++) {
            if(url.charAt(i) == '/') idx = i;
        }
    
        String domain = url.substring(0, idx);
        Integer val = Integer.valueOf(url.substring(idx + 1));
        int num = val;
    
        // 만약 도메인이 처음 나온 도메인이라면 domainToIdx에 갱신합니다.
        if(!domainToIdx.containsKey(domain)) {
            cnt++;
            domainToIdx.put(domain, cnt);
        }
        int domainIdx = domainToIdx.get(domain);
    
        // 도메인 번호에 맞는 레디큐에 숫자 부분을 넣어줍니다.
        isInReadyq[domainIdx].add(num);
    
        // 새로 들어온 url을 도메인에 맞춰 urlPq에 넣어줍니다.
        Url newUrl = new Url(0, 1, num);
        urlPq[domainIdx].add(newUrl);
    
        // 채점 대기 큐에 값이 추가됐으므로 개수를 1 추가합니다.
        ans++;
    }
    
    // 새로운 url을 입력받아 레디큐에 추가해줍니다.
    public static void newUrl(Scanner sc) {
        int tme, id;
        String url;
        tme = sc.nextInt();
        id = sc.nextInt();
        url = sc.next();
    
        // url에서 도메인 부분과 숫자 부분을 나누어줍니다.
        int idx = 0;
        for(int i = 0; i < url.length(); i++) {
            if(url.charAt(i) == '/') idx = i;
        }
    
        String domain = url.substring(0, idx);
        Integer val = Integer.valueOf(url.substring(idx + 1));
        int num = val;

        // 만약 도메인이 처음 나온 도메인이라면 domainToIdx에 갱신합니다.
        if(!domainToIdx.containsKey(domain)) {
            cnt++;
            domainToIdx.put(domain, cnt);
        }
        int domainIdx = domainToIdx.get(domain);
    
        // 만약 숫자 부분이 이미 레디큐에 있으면 중복되므로 넘어갑니다.
        if(isInReadyq[domainIdx].contains(num)) {
            return;
        }

        // 도메인 번호에 맞는 레디큐에 숫자 부분을 넣어줍니다.
        isInReadyq[domainIdx].add(num);
    
        // 새로 들어온 url을 도메인에 맞춰 urlPq에 넣어줍니다.
        Url newUrl = new Url(tme, id, num);
        urlPq[domainIdx].add(newUrl);
    
        // 채점 대기 큐에 값이 추가됐으므로 개수를 1 추가합니다.
        ans++;
    }
    
    // 다음 도메인을 찾아 assign합니다.
    public static void assign(Scanner sc) {
        int tme;
        tme = sc.nextInt();
    
        // 쉬고 있는 채점기가 없다면 넘어갑니다.
        if(restJudger.isEmpty()) return;
    
        // 가장 우선순위가 높은 url을 찾습니다.
        int minDomain = 0;
        Url minUrl = new Url(0, INF, 0);
    
        for(int i = 1; i <= cnt; i++) {
            // 만약 현재 채점중이거나, 현재 시간에 이용할 수 없다면 넘어갑니다.
            if(e[i] > tme) continue;
    
            // 만약 i번 도메인에 해당하는 url이 존재한다면
            // 해당 도메인에서 가장 우선순위가 높은 url을 뽑고 갱신해줍니다.
            if(!urlPq[i].isEmpty()) {
                Url curUrl = urlPq[i].peek();
    
                if(minUrl.id > curUrl.id || (minUrl.id == curUrl.id && minUrl.tme > curUrl.tme)) {
                    minUrl = curUrl;
                    minDomain = i;
                }
            }
        }
    
        // 만약 가장 우선순위가 높은 url이 존재하면
        // 해당 도메인과 쉬고 있는 가장 낮은 번호의 채점기를 연결해줍니다.
        if(minDomain > 0) {
            int judgerIdx = restJudger.peek(); restJudger.poll();
    
            // 해당 도메인의 가장 우선순위가 높은 url을 지웁니다.
            urlPq[minDomain].poll();
    
            // 도메인의 start, end를 갱신해줍니다.
            s[minDomain] = tme;
            e[minDomain] = INF;
    
            // judgerIdx번 채점기가 채점하고 있는 도메인 번호를 갱신해줍니다.
            judgingDomain[judgerIdx] = minDomain;
    
            // 레디큐에서 해당 url의 숫자를 지워줍니다.
            isInReadyq[minDomain].remove(minUrl.num);
    
            // 채점 대기 큐에 값이 지워졌으므로 개수를 1 감소합니다.
            ans--;
        }
    }
    
    // 채점 하나를 마무리합니다.
    public static void finish(Scanner sc) {
        int tme, id;
        tme = sc.nextInt();
        id = sc.nextInt();
    
        // 만약 id번 채점기가 채점 중이 아닐 경우 스킵합니다.
        if(judgingDomain[id] == 0) return;
    
        // id번 채점기를 마무리합니다.
        restJudger.add(id);
        int domainIdx = judgingDomain[id];
        judgingDomain[id] = 0;
    
        // 해당 도메인의 gap, end 값을 갱신해줍니다.
        g[domainIdx] = tme - s[domainIdx];
        e[domainIdx] = s[domainIdx] + 3 * g[domainIdx];
    }
    
    // 현재 채점 대기 큐에 있는 url의 개수를 출력해줍니다.
    public static void check(Scanner sc) {
        int tme;
        tme = sc.nextInt();
    
        System.out.print(ans + "\n");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        q = sc.nextInt();

        for(int i = 1; i <= MAX_D; i++) {
            urlPq[i] = new PriorityQueue<Url>();
        }

        for(int i = 1; i <= MAX_D; i++) {
            isInReadyq[i] = new TreeSet<Integer>();
        }

        while(q-- > 0) {
            int query;
            query = sc.nextInt();

            if(query == 100) {
                // 채점기를 준비합니다.
                init(sc);
            }
            if(query == 200) {
                // 새로운 url을 입력받아 레디큐에 추가해줍니다.
                newUrl(sc);
            }
            if(query == 300) {
                // 다음 도메인을 찾아 assign합니다.
                assign(sc);
            }
            if(query == 400) {
                // 채점 하나를 마무리합니다.
                finish(sc);
            }
            if(query == 500) {
                // 현재 채점 대기 큐에 있는 url의 개수를 출력해줍니다.
                check(sc);
            }
        }
    }
}