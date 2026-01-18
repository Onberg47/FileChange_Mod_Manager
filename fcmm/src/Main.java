/**
 * Author Stephanos B
 * Date: 30/12/2025
 */

/**
 * Main app executable.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 0 && args[0].equals("gui")) {
            gui.App.main(args);
        } else {
            cli.CLIManager.main(args);
        }
    } // psvm()
} // Class