public interface Arbitrator {
    boolean canRead(int domain, int file);
    boolean canWrite(int domain, int file);
    boolean canSwitch(int currentDomain, int targetDomain);
}