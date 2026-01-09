/**
 * Author Stephanos B
 * Date 22/12/2025
*/
package core.utils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import core.config.AppConfig;

/**
 * Logs output to console and a file. Handles logfiles entirly and has various
 * print options.
 * 
 * @author Stephanos B
 */
public class Logger {

    private enum ErrorCodes {
        NOTIFY(0, "Note"),
        WARNING(200, "Warning"),
        NORMAL_ERROR(400, "Error"),
        CRITICAL_ERROR(401, "Unexpected Error");

        int code;
        String name;

        ErrorCodes(int code, String name) {
            this.code = code;
            this.name = name;
        }

        @SuppressWarnings("unused")
        private int getCode() {
            return this.code;
        }

        private String getName() {
            return this.name;
        }

    } // ErrorCodes

    private static AppConfig config = new AppConfig();
    private final Path logPath;
    private final FileWriter logFile;

    private int indent;

    private Logger() {
        Builder build = new Builder().build();
        this.logPath = build.logPath;
        this.logFile = build.logFile;
    }

    private static class Builder {
        Path logPath;
        FileWriter logFile;

        public Builder build() {
            logPath = config.getLogDir().resolve("log__" + DateUtil.getDirDatestamp());

            logFile = null;
            try {
                logFile = new FileWriter(logPath.toFile(), true);

                if (!Files.exists(logPath)) {
                    logFile.append("Log file created at: "
                            + DateUtil.getDisplayTimestamp(LocalDateTime.now())
                            + "\nConfig:\n" + config.toString());
                }
                logFile.append("\nProgram started logging...\n");
            } catch (Exception e) {
                //
            }

            return this;
        }
    } // Builder

    /// /// /// Singleton /// /// ///

    private static volatile Logger instance;

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    /// /// /// Usage Methods /// /// ///

    /**
     * If verboseMsg is not specified, will automatically clone messages to logfile.
     * To not do this, specify verboseMsg as null
     * 
     * @param msg
     */
    public void logEntry(String msg) {
        logEntry(msg, msg);
    }

    public void logEntry(int indent, String msg) {
        this.indent = indent;
        logEntry(msg, msg);
    }

    public void logEntry(int indent, String msg, String verboseMsg) {
        this.indent = indent;
        logEntry(msg, verboseMsg);
    }

    /**
     * 
     * @param msg        Console/user facing message.
     * @param verboseMsg LogFile printed message.
     */
    public void logEntry(String msg, String verboseMsg) {
        if (msg != null)
            System.out.println("\t".repeat(indent) + msg);

        if (verboseMsg != null)
            printToLog(verboseMsg, null, ErrorCodes.NOTIFY);
    }

    ///

    public void logWarning(int indent, String msg, Exception f) {
        this.indent = indent;
        logWarning(msg, f);
    }

    //
    public void logWarning(String msg, Exception f) {
        System.out.println("\t".repeat(indent) + "❗ " + msg + " : " + f.getMessage());
        printToLog(msg, f, ErrorCodes.WARNING);
    }

    ///

    public void logError(String msg, Exception f) {
        System.out.println("❌ " + msg + " : " + f.getMessage());
        printToLog(msg, f, ErrorCodes.NORMAL_ERROR);
    }

    /// /// /// Helpers /// /// ///

    private void printToLog(String msg, Exception f, ErrorCodes code) {
        try {
            if (!Files.exists(logPath))
                throw new FileNotFoundException();

            String str;

            if (f == null) { // No Exception
                str = String.format("[%s] - [%s] : %s\n",
                        DateUtil.getDisplayTimestamp(LocalDateTime.now()),
                        code.getName(),
                        msg);

            } else { // Exception
                String stack = readStackTrace(f);
                str = String.format("[%s] - [%s] [%s] : %s\nStack Trace: %s\n",
                        DateUtil.getDisplayTimestamp(LocalDateTime.now()),
                        code.getName(),
                        f.getClass(),
                        msg,
                        f.getMessage(),
                        stack);
            }

            logFile.append(str);

        } catch (Exception e) {
            System.err.println("Log file error: " + e.getMessage());
        }
    } // printToLog()

    private String readStackTrace(Exception f) {
        if (f.getStackTrace().length <= 0)
            return "";

        StringBuilder stack = new StringBuilder();
        for (StackTraceElement tr : f.getStackTrace()) {
            stack.append("\t" + tr);
        }
        return stack.toString();
    }

} // Class

// ⚠ ⛔ 🪟 ⚙ 🛡 💣 🔒 🔓 🗄 🗃 🗂 🗒 📦 📥 📤 🖥 🎵 🔔 ❗ ⚪ ⚫ ❌ ✔

/*
 * System.exit(0); // Success
 * System.exit(1); // General error
 * System.exit(2); // Invalid arguments
 * System.exit(3); // File not found
 */