import java.util.*;
import java.util.stream.*;
import java.io.*;

public class Main {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    static List<Executor> executors = new ArrayList<>();
    static PriorityQueue<Executor> runnableExecutors = new PriorityQueue<>();

    static Map<Integer, Integer> startTimes = new HashMap<>();
    static Map<Integer, Integer> endTimes = new HashMap<>();
    static Set<Integer> onExecutingDomains = new HashSet<>();

    static class WaitTaskQueue{
        static private Map<Integer, PriorityQueue<Task>> m = new HashMap<>();
        static private int size = 0;

        static public int size(){
            return size;
        }

        static void print(){
            for(Map.Entry<Integer, PriorityQueue<Task>> e : m.entrySet()){
                int domainHash = e.getKey();
                PriorityQueue<Task> pq = e.getValue();
                pq.stream().forEach(x -> System.out.println(x));
            }
        }

        static public void add(Task task){
            PriorityQueue<Task> pq = m.computeIfAbsent(task.domainHash, (key) -> new PriorityQueue<Task>());
            if(pq.contains(task))
                return;
            pq.add(task);
            ++size;
        }

        static public Task poll(int time){
            Task ret = null;
            for(Map.Entry<Integer, PriorityQueue<Task>> e : m.entrySet()){
                int domainHash = e.getKey();
                PriorityQueue<Task> pq = e.getValue();

                if(onExecutingDomains.contains(domainHash))
                    continue;

                Integer startTime = startTimes.get(domainHash);
                Integer endTime = endTimes.get(domainHash);
                if(startTime != null && endTime != null){
                    int gap = endTime - startTime;
                    if(time < startTime + 3 * gap){
                        continue;
                    }
                }
                Task task = pq.peek();
                if(ret == null){
                    ret = task;
                    continue;
                }
                if(task.compareTo(ret) < 0){
                    ret = task;
                }
                
            }
            if(ret != null){
                m.get(ret.domainHash).poll();
                --size;
            }
            return ret;
        }
    }

    static class DomainHash{
        private final static Map<String, Integer> m = new HashMap<>();
        private static Integer idx = 0;

        public static Integer of(String domain){
            return m.computeIfAbsent(domain, (key)-> ++idx);
        }

        public static Integer of(Task task){
            return m.computeIfAbsent(task.domain, (key)-> ++idx);
        }
    }

    static class Task implements Comparable<Task>{
        public int p;
        public String url;
        public String domain;
        public int enterQueueTime;
        public int domainHash;
        public int number;

        Task(int p, String url, int enterQueueTime){
            this.p = p;
            this.url = url;
            this.enterQueueTime = enterQueueTime;
            
            String[] tmp = url.split("/");

            this.domain = tmp[0];
            this.number = Integer.parseInt(tmp[1]);
            this.domainHash = DomainHash.of(domain);
        }


        @Override
        public boolean equals(Object o){
            Task t = (Task) o;
            return this.domainHash == t.domainHash && this.number == t.number;
        }

        @Override
        public int compareTo(Task t){
            if(this.p == t.p)
                return this.enterQueueTime - t.enterQueueTime;
            return this.p - t.p;
        }

        @Override
        public String toString(){
            return String.format("p:%s url:%s t:%d", p, url, enterQueueTime);
        }
    }

    static class Executor implements Comparable<Executor>{
        public Task task;

        public Integer id;

        Executor(Integer id){
            this.id = id;
        }

        boolean isFree(){
            return this.task == null;
        }

        void execute(Task task, int startTime){
            this.task = task;
            onExecutingDomains.add(task.domainHash);
            startTimes.put(task.domainHash, startTime);
            // System.out.println(String.format("[exe]exe:%s task:%s", this, task));
        }

        void done(int endTime){
            if(this.task == null)
                return;
            endTimes.put(task.domainHash, endTime);
            onExecutingDomains.remove(task.domainHash);
            this.task = null;
        }

        @Override
        public int compareTo(Executor exe){
            return Integer.compare(this.id, exe.id);
        }

        @Override
        public String toString(){
            return String.format("id: Task:%s", this.id, this.task);
        }
    }

    String readLine(){
        try{
            return br.readLine();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    int Q;

    void solve(){
        Q = Integer.parseInt(readLine());

        while(Q-- != 0){
            String[] line = readLine().split(" ");
            int oper = Integer.parseInt(line[0]);

            if(oper == 100){
                int N = Integer.parseInt(line[1]);
                String u0 = line[2];

                executors = IntStream.range(0, N+1)
                            .mapToObj((idx) -> new Executor(idx))
                            .collect(Collectors.toList());
                    
                for(int i=1;i<=N;++i)
                    runnableExecutors.add(executors.get(i));
                
                Task task = new Task(1, u0, 0);
                WaitTaskQueue.add(task);
                                // printTaskWaitQueue();
            }

            if(oper == 200){
                int t = Integer.parseInt(line[1]);
                int p = Integer.parseInt(line[2]);
                String u = line[3];

                Task task = new Task(p, u, t);
                WaitTaskQueue.add(task);
            }

            if(oper == 300){
                int t = Integer.parseInt(line[1]);

                if(runnableExecutors.isEmpty())
                    continue;
                if(WaitTaskQueue.size() == 0)
                    continue;

                Task task = WaitTaskQueue.poll(t);
                if(task == null){
                    continue;
                }

                Executor exe = runnableExecutors.poll();
                exe.execute(task, t);
            }

            if(oper == 400){
                int t = Integer.parseInt(line[1]);
                int J_id = Integer.parseInt(line[2]);

                Executor exe = executors.get(J_id);
                exe.done(t);
                runnableExecutors.add(exe);
            }

            if(oper == 500){
                int t = Integer.parseInt(line[1]);
                System.out.println(WaitTaskQueue.size());
            }

        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solve();
    }
}