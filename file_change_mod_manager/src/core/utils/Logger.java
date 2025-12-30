/**
 * Author Stephanos B
 * Date 22/12/2025
*/
package core.utils;

import java.nio.file.Files;
import java.nio.file.Path;

import core.config.AppConfig;

/**
 * Logs output to console and a file. Handles logfiles entirly and has various
 * print options.
 * 
 * @author Stephanos B
 */
public class Logger {
    private static AppConfig config = new AppConfig();

    private static final Path logFile = config.getLogDir().resolve("log__" + DateUtil.getDirTimestamp());

    public static void logEntry(String msg) {
        System.out.println(msg);
    }

    public static void logWarning(String msg, Exception e) {
        System.out.println("❗ " + msg + " : " + e.getMessage());
        printToLog(msg);
    }

    public static void logError(String msg, Exception e) {
        System.out.println("❌ " + msg + " : " + e.getMessage());
        printToLog(msg);
    }

    ///

    private static void printToLog(String msg) {
        // TODO
        if (!Files.exists(logFile)) {
            System.err.println("No log file!");
            return;
        }
    }

} // Class

// ⚠ ⛔ 🪟 ⚙ 🛡 💣 🔒 🔓 🗄 🗃 🗂 🗒 📦 📥 📤 🖥 🎵 🔔 ❗ ⚪ ⚫ ❌ ✔

/*
 * System.exit(0); // Success
 * System.exit(1); // General error
 * System.exit(2); // Invalid arguments
 * System.exit(3); // File not found
 */