import java.util.*;

// Begin code changes by Aden Hunter
public class ObjectList implements Arbitrator {
    private int numDomains;
    private int numFiles;

    private FileObject[] files;

    // Access Lists for Objects
    private List<Map<Integer, String>> fileAccessLists;
    private List<Map<Integer, Boolean>> domainAccessLists;

    public void runTask() {
        Random random = new Random();

        // random numbers between 3 and 7
        numDomains = random.nextInt(5) + 3;
        numFiles = random.nextInt(5) + 3;

        System.out.println("Access control scheme: Access List for Objects");
        System.out.println("Domain Count: " + numDomains);
        System.out.println("Object Count: " + numFiles);

        buildFiles();
        buildAccessLists(random);
        printAccessLists();
        startAgents();
    }

    // creates the file objects used in the task
    private void buildFiles() {
        files = new FileObject[numFiles + 1];

        for (int i = 1; i <= numFiles; i++) {
            files[i] = new FileObject(i);
        }
    }

    // builds the access lists for objects
    // only stores non-empty permissions
    private void buildAccessLists(Random rand) {
        // Initialize lists
        fileAccessLists = new ArrayList<>();
        domainAccessLists = new ArrayList<>();
        fileAccessLists.add(null);
        domainAccessLists.add(null);

        // File access lists
        for (int file = 1; file <= numFiles; file++) {
            Map<Integer, String> list = new HashMap<>();
            for (int domain = 1; domain <= numDomains; domain++) {
                int permission = rand.nextInt(4);
                if (permission == 1) {
                    list.put(domain, "R");
                } else if (permission == 2) {
                    list.put(domain, "W");
                } else if (permission == 3) {
                    list.put(domain, "RW");
                }

            }
            fileAccessLists.add(list);
        }

        // Domain access lists
        for (int targetDomain = 1; targetDomain <= numDomains; targetDomain++) {
            Map<Integer, Boolean> list = new HashMap<>();
            for (int fromDomain = 1; fromDomain <= numDomains; fromDomain++) {
                if (fromDomain != targetDomain && rand.nextBoolean()) {
                    list.put(fromDomain, true);
                }
            }
            domainAccessLists.add(list);
        }
    }

    // prints the access lists
    private void printAccessLists() {
        System.out.println("Access Lists for Objects (populated randomly):");

        // Print file object access lists
        for (int f = 1; f <= numFiles; f++) {
            System.out.printf("File F%-2d : ", f);
            Map<Integer, String> list = fileAccessLists.get(f);
            if (list.isEmpty()) {
                System.out.print("(no permissions assigned)");
            } else {
                for (Map.Entry<Integer, String> entry : list.entrySet()) {
                    System.out.print("D" + entry.getKey() + ":" + entry.getValue() + " ");
                }
            }
            System.out.println();
        }

        // Print domain object access lists
        for (int d = 1; d <= numDomains; d++) {
            System.out.printf("Domain D%-2d (switch from): ", d);
            Map<Integer, Boolean> list = domainAccessLists.get(d);
            if (list.isEmpty()) {
                System.out.print("(no switches allowed)");
            } else {
                for (Integer from : list.keySet()) {
                    System.out.print("D" + from + " ");
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


    @Override
    public boolean canRead(int domain, int file) {
        Map<Integer, String> list = fileAccessLists.get(file);
        String permission = list.getOrDefault(domain, "-");
        return permission.equals("R") || permission.equals("RW");
    }

    @Override
    public boolean canWrite(int domain, int file) {
        Map<Integer, String> list = fileAccessLists.get(file);
        String permission = list.getOrDefault(domain, "-");
        return permission.equals("W") || permission.equals("RW");
    }

    @Override
    public boolean canSwitch(int currentDomain, int targetDomain) {
        Map<Integer, Boolean> list = domainAccessLists.get(targetDomain);
        return Boolean.TRUE.equals(list.get(currentDomain));
    }

    // End code changes by Aden Hunter
}