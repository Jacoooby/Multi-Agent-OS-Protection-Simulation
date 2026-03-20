//Begin code changes by Jacob Berard

// Arbitrator is a shared interface that is used by AgentThread. It allows each of the three tasks to decide how to handle permissions, while still using
// the same permission checking methods.
public interface Arbitrator {
    boolean canRead(int domain, int file);
    boolean canWrite(int domain, int file);
    boolean canSwitch(int currentDomain, int targetDomain);
}
//End code changes by Jacob Berard