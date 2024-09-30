import java.util.*;
import java.util.stream.*;
import java.io.*;

public class Main {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    static List<Executor> executors = new ArrayList<>();
    static List<Task> taskWaitQueue = new ArrayList<>();

    static Map<Task, Integer> startTimes = new HashMap<>();
    static Map<Task, Integer> endTimes = new HashMap<>();
    static Set<Task> onExecutingTasks = new HashSet<>();

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
            this.domainHash = DomainHash.of(domain);

            String[] tmp = url.split("/");
            this.domain = tmp[0];
            this.number = Integer.parseInt(tmp[1]);
        }

        @Override
        public int hashCode(){
            return domainHash;
        }

        @Override
        public boolean equals(Object o){
            return this.domainHash = ((Task)o).domainHash;
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
            onExecutingTasks.add(task);
            startTimes.put(task, startTime);
            // System.out.println(String.format("[exe]exe:%s task:%s", this, task));
        }

        void done(int endTime){
            if(this.task == null)
                return;
            endTimes.put(task, endTime);
            onExecutingTasks.remove(task);
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
                final int t = Integer.parseInt(line[1]);

                List<Executor> li = executors.stream()
                        .skip(1)
                        .filter(exe -> exe.task == null)
                        .sorted(Comparator.comparingInt(x -> x.id))
                        .collect(Collectors.toList());
                
                if(li.isEmpty())
                    continue;
                Executor exe = li.get(0);

                List<Task> tasks = taskWaitQueue.stream()
                            .filter(x -> {
                                Integer s = startTimes.get(x);
                                Integer e = endTimes.get(x);
                                if(s != null && e != null){
                                    int gap = e - s;
                                    return !(t < s + 3 * gap);
                                }
                                return true;
                            })
                            .filter(x -> !onExecutingTasks.contains(x))
                            .sorted()
                            .collect(Collectors.toList());
                if(tasks.isEmpty())
                    continue;
                Task targetTask = tasks.get(0);
                taskWaitQueue = taskWaitQueue.stream()
                                            .filter(x -> x.enterQueueTime != targetTask.enterQueueTime)
                                            .collect(Collectors.toList());
                exe.execute(targetTask, t);
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
            }

        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.solve();
    }
}