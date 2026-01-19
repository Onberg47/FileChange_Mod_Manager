/**
 * Author Stephanos B
 * Date 22/12/2025
*/
package core.utils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
        INFO(0, "Info"),
        SUCCESS(1, "Success"),
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

    private static AppConfig config = AppConfig.getInstance();
    private final Path logPath;
    private final FileWriter writer;

    private int indent;

    private Logger() {
        Builder build = new Builder().build();
        this.logPath = build.logPath;
        this.writer = build.writer;
    }

    private static class Builder {
        Path logPath;
        FileWriter writer;

        public Builder build() {
            logPath = config.getLogDir().resolve("log__" + DateUtil.getDirDatestamp() + ".log");

            boolean fileFound = false;
            if (Files.exists(logPath))
                fileFound = true;

            writer = null;
            try {
                // this creates the file, so .exists() checks need to be done before.
                writer = new FileWriter(logPath.toString(), true);

                if (!fileFound) {
                    writer.write("Log file created at: "
                            + DateUtil.getDisplayTimestamp(LocalDateTime.now())
                            + "\n" + config.toString() + "\n" + "=".repeat(24));
                }
                writer.write("\nProgram started logging...\n");
                writer.flush();
            } catch (Exception e) {
                System.err.println("Log file failed to build: " + e.getMessage());
            }
            return this;
        } // build()
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

    public void close() {
        synchronized (this) {
            info("Closing logger.");
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Error closing logger: " + e.getMessage());
                }
            }
        }
    }
    /// /// /// Usage Methods /// /// ///

    /**
     * If verboseMsg is not specified, will automatically clone messages to logfile.
     * To not do this, specify verboseMsg as null
     * 
     * @param msg
     */
    public void info(String msg) {
        info(msg, msg);
    }

    public void info(int indent, String msg) {
        this.indent = indent;
        info(msg, msg);
    }

    public void info(int indent, String msg, String verboseMsg) {
        this.indent = indent;
        info(msg, verboseMsg);
    }

    /**
     * 
     * @param msg        Console/user facing message.
     * @param verboseMsg LogFile printed message.
     */
    public void info(String msg, String verboseMsg) {
        if (msg != null)
            System.out.println("\t".repeat(indent) + msg);

        if (verboseMsg != null)
            printToLog(verboseMsg, null, ErrorCodes.INFO);
    }

    /// Warning

    public void warning(int indent, String msg, Exception f) {
        this.indent = indent;
        warning(msg, f);
    }

    //
    public void warning(String msg, Exception f) {
        printToLog(msg, f, ErrorCodes.WARNING);
        if (f == null) {
            f = new Exception("No exception passed.");
        }
        System.out.println("\t".repeat(indent) + "❗ " + msg + " : " + f.getMessage());
    }

    /// Error

    public void error(String msg, Exception f) {
        printToLog(msg, f, ErrorCodes.NORMAL_ERROR);
        if (f == null) {
            f = new Exception("No exception passed.");
        }
        System.out.println("❌ " + msg + " : " + f.getMessage());
    }

    /// /// /// Helpers /// /// ///

    private void printToLog(String msg, Exception f, ErrorCodes code) {
        try {
            if (!Files.exists(logPath))
                throw new FileNotFoundException();

            String str = "Error log";

            if (f == null) { // No Exception
                str = String.format("[%s] - [%s] : %s\n",
                        DateUtil.getDisplayTimestamp(LocalDateTime.now()),
                        code.getName(),
                        msg);

            } else { // Exception
                String stack = readStackTrace(f);
                str = String.format("[%s] - [%s] [%s] : %s\n\t%s%s\n",
                        DateUtil.getDisplayTimestamp(LocalDateTime.now()),
                        code.getName(),
                        f.getClass(),
                        msg,
                        f.getMessage(),
                        stack);
            }
            writer.write(str);
            writer.flush();

        } catch (Exception e) {
            System.err.println("Log file error: " + e.getMessage());
        }
    } // printToLog()

    private String readStackTrace(Exception f) {
        if (f.getStackTrace().length <= 0)
            return "";

        StringBuilder stack = new StringBuilder("\n\tStack Trace:\n");
        for (StackTraceElement tr : f.getStackTrace()) {
            stack.append("\t\t" + tr.toString() + "\n");
        }
        return stack.toString();
    }

    /// /// /// Public utilities /// /// ///

    /**
     * 
     * @param i   initial value
     * @param max max value
     * @return A String displaying the percentage and a progress bar.
     */
    public static String progressBar(double i, double max) {
        double charScale = 50.0; // total number of characters
        i = (i / max) * charScale;
        return String.format("Progress: %.0f%% [%s%s]\n",
                ((i / charScale) * 100),
                "#".repeat((int) i),
                "_".repeat((int) (charScale - i)));
    }

} // Class

// ⚠ ⛔ 🪟 ⚙ 🛡 💣 🔒 🔓 🗄 🗃 🗂 🗒 📦 📥 📤 🖥 🎵 🔔 ❗ ⚪ ⚫ ❌ ✔

/*
 * System.exit(0); // Success
 * System.exit(1); // General error
 * System.exit(2); // Invalid arguments
 * System.exit(3); // File not found
 */