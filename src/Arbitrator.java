//Begin code changes by Jacob Berard
public interface Arbitrator {
    boolean canRead(int domain, int file);
    boolean canWrite(int domain, int file);
    boolean canSwitch(int currentDomain, int targetDomain);
}
//End code changes by Jacob Berard