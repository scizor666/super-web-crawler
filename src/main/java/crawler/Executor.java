package crawler;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by atepliashin on 5/26/16.
 */
public class Executor {

    public static final int DEFAULT_DEPTH = 1;
    public static final int DEFAULT_THREADS_NUMBER = 4;

    private static Executor instance;

    private Queue<URI> uriQueue = new ConcurrentLinkedQueue<>();
    private Map<URI, Integer> uriMap = new ConcurrentHashMap<>();

    private static URI uri;
    private static int depth = DEFAULT_DEPTH;
    private static int maxThreads = DEFAULT_THREADS_NUMBER;
    private List<Thread> threads = new ArrayList<>();

    private Executor() {

    }

    private Executor(URI uri, int depth, int maxThreads) {
        Executor.uri = uri;
        Executor.depth = depth;
        Executor.maxThreads = maxThreads;
        uriQueue.add(uri);
        uriMap.put(uri, depth);
    }

    public static Executor instance() {
        if (instance == null) {
            instance = new Executor();
        }
        return instance;
    }

    public static Executor instance(URI uri, int depth, int threads) {
        if (instance == null || Executor.uri != uri || Executor.depth != depth || Executor.maxThreads != threads) {
            instance = new Executor(uri, depth, threads);
        }
        return instance;
    }

    public Queue<URI> getUriQueue() {
        return uriQueue;
    }

    public Map<URI, Integer> getUriMap() {
        return uriMap;
    }

    public void start() {
        while (!uriQueue.isEmpty()) {
            if (threads.size() < maxThreads) {
                URI currentUri = uriQueue.remove();
                int currentDepth = uriMap.get(currentUri);
                Thread thread = new Thread(new Crawler(currentUri, currentDepth));
                thread.start();
                threads.add(thread);
            }
            List<Thread> finishedThreads = new ArrayList<>();
            threads.forEach(thread -> {
                if (!thread.isAlive()) {
                    finishedThreads.add(thread);
                }
            });
            try {
                synchronized (instance) {
                    instance.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                // do something
            }
            finishedThreads.forEach(thread -> threads.remove(thread));
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                // do something
            }
        }
    }
}
