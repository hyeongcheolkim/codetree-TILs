import java.util.*;
import java.util.stream.*;
import java.io.*;

public class Main {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    static List<Executor> executors = new ArrayList<>();
    static PriorityQueue<Executor> runnableExecutors = new PriorityQueue<>();

    static Map<String, Integer> startTimes = new HashMap<>();
    static Map<String, Integer> endTimes = new HashMap<>();
    static Set<Integer> onExecutingDomains = new HashSet<>();

    static class TaskWaitQueue{

        private int size = 0;
        private Map<Task, PriorityQueue<Task>> tasks;

        public int size(){
            return this.size;
        }

        public void add(Task task){
            tasks.computeIfAbsent(task, new PriorityQueue<>())
                    .add(task.number);
            ++size;
        }

        public Task poll(int time){
            for(int domainHash : DomainHash.hashValues()){
                

                for(PriorityQueue<Task> pq : tasks.getOrDefault(val)){

                }
            }
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

        public int[] hashValues(){
            return this.m.values();
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
        public int hashCode(){
            return domainHash;
        }

        @Override
        public boolean equals(Object o){
            return this.domainHash == ((Task) o).domainHash;
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

    void printTaskWaitQueue(){
        List<Task> tmp = new ArrayList<>();

        while(!taskWaitQueue.isEmpty()){
            Task task = taskWaitQueue.poll();
            System.out.println(task);
            tmp.add(task);
        }
        taskWaitQueue.addAll(tmp);
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
                taskWaitQueue.add(task);
                                // printTaskWaitQueue();
            }

            if(oper == 200){
                int t = Integer.parseInt(line[1]);
                int p = Integer.parseInt(line[2]);
                String u = line[3];

                Task task = new Task(p, u, t);
                if(taskWaitQueue.stream().anyMatch(x -> x.domainHash == task.domainHash && x.number == task.number))
                    continue;
                taskWaitQueue.add(task);
            }

            if(oper == 300){
                int t = Integer.parseInt(line[1]);

                if(runnableExecutors.isEmpty())
                    continue;

                List<Task> tmp = new ArrayList<>();
                while(!taskWaitQueue.isEmpty()){
                    Task task = taskWaitQueue.poll();

                    if(onExecutingDomains.contains(task.domainHash)){
                        tmp.add(task);
                        continue;
                    }

                    Integer s = startTimes.get(task);
                    Integer e = endTimes.get(task);


                    if(s != null && e != null){
                        int gap = e - s;
                        if(t < s + 3 * gap){
                            tmp.add(task);
                            continue;
                        }
                    }

                    Executor exe = runnableExecutors.poll();
                    exe.execute(task, t);
                    break;
                }
                taskWaitQueue.addAll(tmp);
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

                System.out.println(taskWaitQueue.size());
            }

        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solve();
    }
}