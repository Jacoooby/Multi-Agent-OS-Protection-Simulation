import java.util.*;

//Begin code changes by Jacob Berard

public class DomainList implements Arbitrator {
    // N = number of domains
    private int numDomains;
    // M = number of files
    private int numFiles;
    private FileObject[] files;

    // list for file access
    private List<Map<Integer, String>> fileDomainLists;
    // list for domain switching access
    private List<Map<Integer, Boolean>> domainSwitches;


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
        // Initialize lists
        fileDomainLists = new ArrayList<>();
        domainSwitches = new ArrayList<>();
        fileDomainLists.add(null);
        domainSwitches.add(null);

        // apply random permissions at each domain for each file
        for (int d = 1; d <= numDomains; d++) {
            Map<Integer, String> fileMap = new HashMap<>();
            Map<Integer, Boolean> domainMap = new HashMap<>();

            for (int f = 1; f <= numFiles; f++) {
                // random int used to determine a permission to give
                int permission = rand.nextInt(4);

                // at domain i, give a random permission for each file
                if (permission == 0) {
                    fileMap.put(f, "-");
                } else if (permission == 1) {
                    fileMap.put(f, "R");
                } else if (permission == 2) {
                    fileMap.put(f, "W");
                } else {
                    fileMap.put(f, "RW");
                }
            }

            // randomly decide which domains the current domain can switch to
            for (int target = 1; target <= numDomains; target++) {
                if (d == target) {
                    // ensures domain cannot switch to itself
                    domainMap.put(target, false);
                } else {
                    domainMap.put(target, rand.nextBoolean());
                }
            }
            // add file permissions and domain switch permissions to the lists
            fileDomainLists.add(fileMap);
            domainSwitches.add(domainMap);
        }
    }

    // prints the capability lists for each domain
    private void printCapabilityLists() {
        System.out.println();
        System.out.println("Capability List for Domains:");

        for (int d = 1; d <= numDomains; d++) {
            System.out.println("\nD" + d + ":");

            Map<Integer, String> fileMap = fileDomainLists.get(d);
            Map<Integer, Boolean> domainMap = domainSwitches.get(d);

            // print file permissions for domain
            for (int f = 1; f <= numFiles; f++) {
                String permission = fileMap.get(f);
                if (permission != null && !permission.equals("-")) {
                    System.out.print(" F" + f + "-> " + permission + "  ");
                }
            }

            // print domain switch permissions for domain
            for (int i = 1; i <= numDomains; i++) {
                Boolean canSwitch = domainMap.get(i);
                if (canSwitch != null && canSwitch) {
                    System.out.print(" D" + i + " -> allow" + "  ");
                }
            }
            System.out.println();
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
        String permission = fileDomainLists.get(domain).get(file);
        return permission.equals("R") || permission.equals("RW");
    }

    // returns true if the domain has write or read/write permission on a file
    @Override
    public boolean canWrite(int domain, int file) {
        String permission = fileDomainLists.get(domain).get(file);
        return permission.equals("W") || permission.equals("RW");
    }

    // returns true if the current domain can switch to the target domain
    @Override
    public boolean canSwitch(int currentDomain, int targetDomain) {
        Boolean canSwitch = domainSwitches.get(currentDomain).get(targetDomain);
        return Boolean.TRUE.equals(canSwitch);
    }
}
//End code changes by Jacob Berard