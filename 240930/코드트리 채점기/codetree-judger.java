import java.util.*;
import java.util.stream.*;
import java.io.*;

public class Main {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    static List<Executor> executors = new ArrayList<>();
    static Map<Task, Integer> startTimes = new HashMap<>();
    static Map<Task, Integer> endTimes = new HashMap<>();
    static Set<Task> onExecutingTasks = new HashSet<>();
    static PriorityQueue<Executor> runnableExecutors = new PriorityQueue<Executor>();
    static PriorityQueue<Task> taskWaitQueue = new PriorityQueue<Task>();

    static String extractDomainFromUrl(String url){
        String[] tmp = url.split("/");
        return tmp[0];
    }

    static class Task implements Comparable<Task>{
        public int p;
        public String url;
        public String domain;
        public int enterQueueTime;

        Task(int p, String url, int enterQueueTime){
            this.p = p;
            this.url = url;
            this.enterQueueTime = enterQueueTime;
            this.domain = extractDomainFromUrl(url);
        }

        @Override
        public int hashCode(){
            return Objects.hash(domain);
        }

        @Override
        public boolean equals(Object o){
            return this.domain.equals(((Task) o).domain);
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
            if(id >= 1)
                runnableExecutors.add(this);
        }

        boolean isFree(){
            return this.task == null;
        }

        void execute(Task task, int startTime){
            this.task = task;
            onExecutingTasks.add(task);
            startTimes.put(task, startTime);
            // System.out.println(String.format("[exe]exe:%s task:%s", this, task));
        }

        void done(int endTime){
            if(this.task == null)
                return;
            endTimes.put(task, endTime);
            onExecutingTasks.remove(task);
            runnableExecutors.add(this);
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
                
                Task task = new Task(1, u0, 0);
                taskWaitQueue.add(task);
                                // printTaskWaitQueue();
            }

            if(oper == 200){
                int t = Integer.parseInt(line[1]);
                int p = Integer.parseInt(line[2]);
                String u = line[3];

                Task task = new Task(p, u, t);
                if(taskWaitQueue.stream().anyMatch(x -> x.url.equals(u)))
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

                    if(onExecutingTasks.contains(task)){
                        tmp.add(task);
                        continue;
                    }

                    Integer s = startTimes.get(task);
                    Integer e = endTimes.get(task);


                    // System.out.println(String.format("task:%s s:%d e:%d", task, s, e));
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
            }

            if(oper == 500){
                int t = Integer.parseInt(line[1]);

                System.out.println(taskWaitQueue.size());
                // printTaskWaitQueue();
            }

        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solve();
    }
}