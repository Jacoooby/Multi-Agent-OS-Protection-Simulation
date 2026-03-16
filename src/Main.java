public class Main {
    public static void main(String[] args) {
        String task;

        if (args.length != 2) {
            System.out.println("To start a task run the program with one of the following arguments: -S 1, -S 2, or -S 3 \n" +
                    "-S 1 Access Matrix \n" +
                    "-S 2 Access List for Objects \n" +
                    "-S 3 to run Capability List for Domains");
            return;
        }

        // check args at index 0 to see if it starts with -S
        if (!args[0].equals("-S")) {
            System.out.println("Invalid argument to start a task. Try again with either -S 1, -S 2, or -S 3");
            return;
        }

        task = args[1];

        // check args at index 1 to see if user typed 1, 2, or 3
        if (!task.equals("1") && !task.equals("2") && !task.equals("3")) {
            System.out.println("Invalid argument to start a task. Try again with either -S 1, -S 2, or -S 3");
            return;
        }


        // switch statement to manually determine what task to run depending on what the user types.
        switch (task) {
            case "1":
                System.out.println("Starting Task 1: Access Matrix");
                startAccessMatrix();
                break;

            case "2":
                System.out.println("Starting Task 2: Access List for Objects");
                startObjectList();
                break;

            case "3":
                System.out.println("Starting Task 3: Capability List for Domains");
                startDomainList();
                break;
        }
    }


    public static void startAccessMatrix() {
        return;
    }

    public static void startObjectList() {
        return;
    }

    public static void startDomainList() {
        DomainList task3 = new DomainList();
        task3.runSimulation();
    }
}
