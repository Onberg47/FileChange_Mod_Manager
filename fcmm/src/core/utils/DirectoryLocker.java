/**
 * Author Stephanos B
 * Date 19/01/2026
 */
package core.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import core.config.AppConfig;
import core.config.AppPreferences.properties;

/**
 * @since v4.0.1
 */
public class DirectoryLocker {
    private final Path lockFilePath;
    private final Path lockDir;
    private FileLock fileLock;
    private FileChannel lockChannel;
    private final ReentrantLock jvmLock = new ReentrantLock();

    // Track locks by their canonical path
    private static final Map<Path, DirectoryLocker> activeLocks = new ConcurrentHashMap<>();

    public DirectoryLocker(Path dir) {
        this.lockDir = dir.toAbsolutePath();
        this.lockFilePath = lockDir.resolve(".lock");
    }

    public boolean acquireLock() {
        // Check if already locked in this JVM
        if (activeLocks.containsKey(lockDir)) {
            return false; // Already locked
        }

        // Layer 1: JVM-level lock (prevents multiple threads)
        if (!jvmLock.tryLock()) {
            return false;
        }

        if (AppConfig.getInstance().preferences.getAsBoolean(properties.FS_LOCKS.key(), (boolean) properties.FS_LOCKS.getDefaultValue())) {
            try {
                // Layer 2: File system lock
                boolean acquired = acquireFileSystemLock();
                if (acquired) {
                    activeLocks.put(lockDir, this);
                }
                return acquired;
            } catch (Exception e) {
                jvmLock.unlock();
                return false;
            }
        } else
            return true;
    }

    private boolean acquireFileSystemLock() {
        try {
            // Create parent directories
            Files.createDirectories(lockFilePath.getParent());

            // Open/create lock file - use only ONE lock file
            lockChannel = FileChannel.open(lockFilePath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.DELETE_ON_CLOSE);

            // Try to get exclusive lock (non-blocking)
            fileLock = lockChannel.tryLock(0, Long.MAX_VALUE, false);

            if (fileLock != null) {
                // Write PID and info to lock file
                writeLockInfo();
                return true;
            }

            // Lock exists - check if stale
            return checkAndCleanStaleLock();

        } catch (IOException e) {
            // File locking not supported
            return fallbackLock();
        }
    }

    private boolean checkAndCleanStaleLock() {
        try {
            // Try to read existing lock info
            if (!Files.exists(lockFilePath)) {
                return false; // No lock file
            }

            String content;
            try (FileChannel channel = FileChannel.open(lockFilePath,
                    StandardOpenOption.READ)) {
                // Read entire file
                long size = channel.size();
                if (size == 0) {
                    // Empty lock file - stale
                    Files.delete(lockFilePath);
                    return acquireFileSystemLock();
                }

                java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate((int) size);
                channel.read(buffer);
                content = new String(buffer.array());
            }

            // Parse lock info
            LockInfo info = parseLockInfo(content);
            if (info == null) {
                // Invalid format - delete stale lock
                Files.delete(lockFilePath);
                return acquireFileSystemLock();
            }

            long currentTime = System.currentTimeMillis();
            long lockAge = currentTime - info.timestamp;

            // Lock is stale if older than 5 minutes
            if (lockAge > 300000) {
                Files.delete(lockFilePath);
                return acquireFileSystemLock();
            }

            // Check if process is still alive
            if (isProcessAlive(info.pid)) {
                return false; // Valid lock held by another process
            } else {
                // Process is dead - stale lock
                Files.delete(lockFilePath);
                return acquireFileSystemLock();
            }

        } catch (Exception e) {
            return false;
        }
    }

    private boolean fallbackLock() {
        // Fallback using only the main .lock file (no .lock.pid)
        try {
            String pid = getProcessId();
            String content = createLockInfo(pid);

            // Try to create lock file atomically
            Files.write(lockFilePath, content.getBytes(),
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE);

            return true;

        } catch (FileAlreadyExistsException e) {
            // Lock exists - check if stale
            return checkAndCleanStaleLock();
        } catch (IOException e) {
            return false;
        }
    }

    public void releaseLock() {
        try {
            // Remove from active locks first
            activeLocks.remove(lockDir);

            if (fileLock != null) {
                fileLock.release();
            }
            if (lockChannel != null) {
                lockChannel.close();
            }

            // Clean up lock file
            Files.deleteIfExists(lockFilePath);

        } catch (IOException e) {
            // Ignore cleanup errors
        } finally {
            jvmLock.unlock();
        }
    }

    private void writeLockInfo() throws IOException {
        String info = createLockInfo(getProcessId());
        lockChannel.position(0);
        lockChannel.write(java.nio.ByteBuffer.wrap(info.getBytes()));
    }

    private String createLockInfo(String pid) {
        return String.format(
                "pid=%s%ntimestamp=%d%nthread=%s%nhost=%s%n",
                pid,
                System.currentTimeMillis(),
                Thread.currentThread().getName(),
                getHostName());
    }

    private LockInfo parseLockInfo(String content) {
        try {
            String pid = null;
            long timestamp = 0;

            for (String line : content.split("\n")) {
                if (line.startsWith("pid=")) {
                    pid = line.substring(4);
                } else if (line.startsWith("timestamp=")) {
                    timestamp = Long.parseLong(line.substring(10));
                }
            }

            if (pid != null && timestamp > 0) {
                return new LockInfo(pid, timestamp);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static class LockInfo {
        final String pid;
        final long timestamp;

        LockInfo(String pid, long timestamp) {
            this.pid = pid;
            this.timestamp = timestamp;
        }
    }

    private String getProcessId() {
        try {
            String processName = java.lang.management.ManagementFactory
                    .getRuntimeMXBean().getName();
            return processName.split("@")[0];
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private boolean isUnix() {
        return !System.getProperty("os.name").toLowerCase().contains("win");
    }

    private boolean isProcessAlive(String pid) {
        if ("unknown".equals(pid)) {
            return false; // Can't check unknown PID
        }

        if (!isUnix()) {
            // Windows process checking
            try {
                Process process = new ProcessBuilder("tasklist", "/FI",
                        "PID eq " + pid).start();
                String output = new String(process.getInputStream().readAllBytes());
                return output.contains(pid);
            } catch (Exception e) {
                return true; // Assume alive if can't check
            }
        }

        // Unix/Linux
        try {
            Process process = new ProcessBuilder("ps", "-p", pid).start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return true; // Assume alive if can't check
        }
    }

    // Static utility methods for convenience
    public static boolean lockDirectory(Path dir) {
        DirectoryLocker locker = new DirectoryLocker(dir);
        return locker.acquireLock();
    }

    public static void unlockDirectory(Path dir) {
        DirectoryLocker locker = activeLocks.get(dir.toAbsolutePath());
        if (locker != null) {
            locker.releaseLock();
        }
    }

    public static boolean isDirectoryLocked(Path dir) {
        return activeLocks.containsKey(dir.toAbsolutePath());
    }
} // Class