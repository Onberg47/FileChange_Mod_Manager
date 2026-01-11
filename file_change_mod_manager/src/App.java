/**
 * Author Stephanos B
 * Date: 30/12/2025
 */

//import core.utils.Logger;

/**
 * Main app executable.
 */
public class App {
    public static void main(String[] args) throws Exception {
        if (args.length != 0 && args[0].equals("GUI")) {
            gui.App.main(args);
        } else {
            cli.CLIManager.main(args);
        }
        //Logger.getInstance().close(); // this gets closed when the GUI starts!
    } // psvm()
} // Class