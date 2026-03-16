import java.util.Random;

// AgentThread class where each agent thread will make 5 random requests.
public class AgentThread extends Thread {
    private int agentId;
    private int currentDomain;
    // N = number of domains
    private int numDomains;
    // M = number of files
    private int numFiles;
    private FileObject[] files;
    private Arbitrator arbitrator;
    private Random rand;

    // initialize agent with ID, starting domain, number of domain and files, and arbitrator
    public AgentThread(int agentId, int startingDomain, int numDomains, int numFiles, FileObject[] files, Arbitrator arbitrator) {
        this.agentId = agentId;
        this.currentDomain = startingDomain;
        this.numDomains = numDomains;
        this.numFiles = numFiles;
        this.files = files;
        this.arbitrator = arbitrator;
        this.rand = new Random();
    }

    // each agent makes 5 requests, yielding after each one
    @Override
    public void run() {
        for (int requestNum = 1; requestNum <= 5; requestNum++) {
            makeRequest();
            agentYield();
        }
    }

    // randomly perform either read/write request or a domain switch request
    private void makeRequest() {
        int x = rand.nextInt(numFiles + numDomains - 1);

        // file request
        if (x < numFiles) {
            int fileId = x + 1;
            int y = rand.nextInt(2);

            // read request
            if (y == 0) {
                System.out.println("Agent " + agentId + " in D" + currentDomain +
                        " requests READ on F" + fileId);

                boolean allowed = arbitrator.canRead(currentDomain, fileId);

                if (allowed) {
                    System.out.println("Arbitrator decision for Agent " + agentId + ": GRANTED");
                    files[fileId].readFile(agentId, currentDomain);
                } else {
                    System.out.println("Arbitrator decision for Agent " + agentId + ": DENIED");
                }
            }

            // write request
            else {
                System.out.println("Agent " + agentId + " in D" + currentDomain + " requests WRITE on F" + fileId);

                boolean allowed = arbitrator.canWrite(currentDomain, fileId);

                if (allowed) {
                    System.out.println("Arbitrator decision for Agent " + agentId + ": GRANTED");
                    files[fileId].writeFile(agentId, currentDomain);
                } else {
                    System.out.println("Arbitrator decision for Agent " + agentId + ": DENIED");
                }
            }
        }

        // domain switch request
        else {
            int targetDomain = (x - numFiles) + 1;

            if (targetDomain >= 1 && targetDomain <= numDomains && targetDomain != currentDomain) {
                System.out.println("Agent " + agentId + " in D" + currentDomain + " requests switch to D" + targetDomain);

                boolean allowed = arbitrator.canSwitch(currentDomain, targetDomain);

                if (allowed) {
                    System.out.println("Arbitrator decision for Agent " + agentId + ": GRANTED");
                    currentDomain = targetDomain;
                    System.out.println("Agent " + agentId + " switched to D" + currentDomain);
                } else {
                    System.out.println("Arbitrator decision for Agent " + agentId + ": DENIED");
                }
            } else {
                System.out.println("Agent " + agentId + " attempted invalid switch.");
                System.out.println("Arbitrator decision for Agent " + agentId + ": DENIED");
            }
        }
    }

    // agent yields for 3 to 7 cycles after each request
    private void agentYield() {
        int cycles = rand.nextInt(5) + 3;
        System.out.println("Agent " + agentId + " yields for " + cycles + " cycles");

        try {
            Thread.sleep(cycles * 100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}