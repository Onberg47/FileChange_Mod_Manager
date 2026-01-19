/**
 * Date 19/01/2026
 */
package core.managers;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import core.utils.DirectoryLocker;
import core.utils.Logger;

/**
 * Provides DirectoryLock usage methods and lock-instance tracking. Has various
 * short-cut methods for ease-of-use.
 * 
 * @since v4.0.1
 */
public class LockManager {
    private static final Logger log = Logger.getInstance();

    private static final ConcurrentHashMap<String, DirectoryLocker> locks = new ConcurrentHashMap<>(0);

    public static boolean lockDirectory(String lockId, Path dir) {
        if (locks.containsKey(lockId)) {
            log.warning("Lock already on Directory: " + dir, null);
            return false;
        }

        DirectoryLocker locker = new DirectoryLocker(dir);
        if (locker.acquireLock()) {
            locks.put(lockId, locker);
            log.info("🔒 Lock granted on Directory: " + dir);
            return true;
        }
        log.warning("Could not lock Directory: " + dir, null);
        return false;
    }

    public static void unlockDirectory(String lockId) {
        DirectoryLocker locker = locks.remove(lockId);
        if (locker != null) {
            locker.releaseLock();
            log.info("🔓 Lock released: " + lockId);
        }
    }

    public static boolean isLocked(String lockId) {
        return locks.containsKey(lockId);
    }

    /// /// /// Special / short-cut methods /// /// ///

    /// /// Games
    public static boolean lockGame(String gameId, Path gameDir) {
        String key = "game:" + gameId;
        return lockDirectory(key, gameDir);
    }

    public static void unlockGame(String gameId) {
        String key = "game:" + gameId;
        unlockDirectory(key);
    }

    /// /// Temporary directories (auto-generated key)

    public static boolean lockTempDir(Path tempDir) {
        String key = "temp:" + tempDir.toAbsolutePath().toString();
        return lockDirectory(key, tempDir);
    }

    public static void unlockTempDir(Path tempDir) {
        String key = "temp:" + tempDir.toAbsolutePath().toString();
        unlockDirectory(key);
    }

} // Class