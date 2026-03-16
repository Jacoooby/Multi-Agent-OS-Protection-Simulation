import java.util.concurrent.locks.ReentrantLock;

// File object used by agents in the task. Each file has content that can be read or written and a lock
// for synchronization.
public class FileObject {
    private int id;
    private String content;
    private ReentrantLock lock;

    // initialize file with an ID, starting content, and a lock
    public FileObject(int id) {
        this.id = id;
        this.content = "File " + id + " start";
        this.lock = new ReentrantLock();
    }

    // returns the file id
    public int getId() {
        return id;
    }

    // read from the file while holding the lock
    public void readFile(int agentId, int domainId) {
        lock.lock();
        try {
            System.out.println("Agent " + agentId + " in D" + domainId + " READS F" + id + " -> " + content);
        } finally {
            lock.unlock();
        }
    }

    // write to the file while holding the lock
    public void writeFile(int agentId, int domainId) {
        lock.lock();
        try {
            content = content +  " [written by A" + agentId + "]";
            System.out.println("Agent " + agentId + " in D" + domainId + " WRITES F" + id + " -> " + content);
        } finally {
            lock.unlock();
        }
    }
}