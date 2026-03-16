import java.util.Random;

public class AccessMatrix implements Arbitrator {
    private int numDomains;
    private int numFiles;

    private FileObject[] files;
    private String[][] accessMatrix;

    public void runTask() {
        Random random = new Random();

        // randomly generate the number of domains and files between 3 and 7
        numDomains = random.nextInt(5) + 3;
        numFiles = random.nextInt(5) + 3;

        System.out.println("Access control scheme: Access Matrix");
        System.out.println("Domain Count: " + numDomains);
        System.out.println("Object Count: " + numFiles);

        buildFiles();
        buildAccessMatrix(random);
        printAccessMatrix();
        startAgents();
    }

    // creates the file objects used in the task
    private void buildFiles() {
        files = new FileObject[numFiles + 1];

        for (int i = 1; i <= numFiles; i++) {
            files[i] = new FileObject(i);
        }
    }

    // builds the access matrix with random permissions
    private void buildAccessMatrix(Random rand) {
        accessMatrix = new String[numDomains + 1][numFiles + numDomains + 1];

        for (int domain = 1; domain <= numDomains; domain++) {

            // file permissions
            for (int file = 1; file <= numFiles; file++) {
                int permission = rand.nextInt(4);

                if (permission == 0) {
                    accessMatrix[domain][file] = "-";
                } else if (permission == 1) {
                    accessMatrix[domain][file] = "R";
                } else if (permission == 2) {
                    accessMatrix[domain][file] = "W";
                } else {
                    accessMatrix[domain][file] = "RW";
                }
            }

            // domain switch permissions
            for (int targetDomain = 1; targetDomain <= numDomains; targetDomain++) {
                int column = numFiles + targetDomain;

                if (domain == targetDomain) {
                    accessMatrix[domain][column] = "N/A";
                } else {
                    if (rand.nextBoolean()) {
                        accessMatrix[domain][column] = "allow";
                    } else {
                        accessMatrix[domain][column] = "-";
                    }
                }
            }
        }
    }

    // prints the access matrix
    private void printAccessMatrix() {
        System.out.printf("%-15s", "Domain/Object");

        // print file headers
        for (int file = 1; file <= numFiles; file++) {
            System.out.printf("%-8s", "F" + file);
        }

        // print domain headers
        for (int domain = 1; domain <= numDomains; domain++) {
            System.out.printf("%-8s", "D" + domain);
        }

        System.out.println();

        // print matrix rows
        for (int domain = 1; domain <= numDomains; domain++) {
            System.out.printf("%-15s", "D" + domain);

            for (int col = 1; col <= numFiles + numDomains; col++) {
                System.out.printf("%-8s", accessMatrix[domain][col]);
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
        String permission = accessMatrix[domain][file];
        return permission.equals("R") || permission.equals("RW");
    }

    // returns true if the domain has write or read/write permission on a file
    @Override
    public boolean canWrite(int domain, int file) {
        String permission = accessMatrix[domain][file];
        return permission.equals("W") || permission.equals("RW");
    }

    // returns true if the current domain can switch to the target domain
    @Override
    public boolean canSwitch(int currentDomain, int targetDomain) {
        int column = numFiles + targetDomain;
        return accessMatrix[currentDomain][column].equals("allow");
    }
}