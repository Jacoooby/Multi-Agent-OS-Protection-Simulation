import java.util.Random;

public class DomainList implements Arbitrator {
    // N = number of domains
    private int numDomains;
    // M = number of files
    private int numFiles;
    private FileObject[] files;

    // filePermissions[i][j] stores the permission for domain i on file j
    private String[][] filePermissions;

    // domainSwitches[i][j] is true if domain i can switch to domain j
    private boolean[][] domainSwitches;

    public void runTask() {
        Random rand = new Random();

        // randomly generate the number of domains and files between 3 and 7
        numDomains = rand.nextInt(5) + 3;
        numFiles = rand.nextInt(5) + 3;

        System.out.println("Started: Capability List for Domains.");
        System.out.println("Number of Domains: " + numDomains);
        System.out.println("Number of Files: " + numFiles);

        buildFiles();
        buildCapabilityLists(rand);
        printCapabilityLists();
        startAgents();
    }

    // creates the file objects used in the task
    private void buildFiles() {
        files = new FileObject[numFiles + 1];
        for (int i = 1; i <= numFiles; i++) {
            files[i] = new FileObject(i);
        }
    }

    // builds the capability lists for each domain
    private void buildCapabilityLists(Random rand) {
        filePermissions = new String[numDomains + 1][numFiles + 1];
        domainSwitches = new boolean[numDomains + 1][numDomains + 1];

        for (int i = 1; i <= numDomains; i++) {
            for (int j = 1; j <= numFiles; j++) {
                // random int used to determine a permission to give
                int permission = rand.nextInt(4);

                // at domain i, give a random permission for each file
                if (permission == 0) {
                    filePermissions[i][j] = "-";
                } else if (permission == 1) {
                    filePermissions[i][j] = "R";
                } else if (permission == 2) {
                    filePermissions[i][j] = "W";
                } else {
                    filePermissions[i][j] = "RW";
                }
            }

            // randomly decide which domains this domain can switch to
            for (int target = 1; target <= numDomains; target++) {
                if (i == target) {
                    // ensures domain cannot switch to itself
                    domainSwitches[i][target] = false;
                } else {
                    domainSwitches[i][target] = rand.nextBoolean();
                }
            }
        }
    }

    // prints the capability lists for each domain
    private void printCapabilityLists() {
        System.out.println();
        System.out.println("Capability List for Domains:");

        for (int i = 1; i <= numDomains; i++) {
            System.out.println("\nD" + i + ":");

            // prints file permissions for domain i
            for (int j = 1; j <= numFiles; j++) {
                if (!filePermissions[i][j].equals("-")) {
                    System.out.println(" F" + j + " -> " + filePermissions[i][j]);
                }
            }

            // prints domain switch permissions for domain i
            for (int target = 1; target <= numDomains; target++) {
                if (domainSwitches[i][target]) {
                    System.out.println(" D" + target + " -> allow");
                }
            }
        }
        System.out.println();
    }

    // creates one agent for each domain and starts all agent threads
    private void startAgents() {
        AgentThread[] agents = new AgentThread[numDomains + 1];

        // each agent starts in its own domain
        for (int d = 1; d <= numDomains; d++) {
            agents[d] = new AgentThread(d, d, numDomains, numFiles, files, this);
            agents[d].start();
        }

        // wait until all agents finish
        for (int d = 1; d <= numDomains; d++) {
            try {
                agents[d].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // returns true if the domain has read or read/write permission on a file
    @Override
    public boolean canRead(int domain, int file) {
        return filePermissions[domain][file].equals("R") ||
                filePermissions[domain][file].equals("RW");
    }

    // returns true if the domain has write or read/write permission on a file
    @Override
    public boolean canWrite(int domain, int file) {
        return filePermissions[domain][file].equals("W") ||
                filePermissions[domain][file].equals("RW");
    }

    // returns true if the current domain can switch to the target domain
    @Override
    public boolean canSwitch(int currentDomain, int targetDomain) {
        return domainSwitches[currentDomain][targetDomain];
    }
}