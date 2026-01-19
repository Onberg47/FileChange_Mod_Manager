/**
 * Date 19/01/2026
 */

import java.nio.file.Path;

import core.managers.LockManager;
import core.utils.DirectoryLocker;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Test DirecotryLocker and LockManager
 * 
 * @since v4.0.1
 */
public class LocksTest {

    public static void testCrossProcessLocking(Path testDir) throws Exception {
        System.out.println("=== Testing Directory Locking ===");

        // Ensure test directory exists
        Files.createDirectories(testDir);

        // Test 1: Single locker should work
        System.out.print("Test 1 - Acquire lock: ");
        DirectoryLocker locker1 = new DirectoryLocker(testDir);
        assertTrue(locker1.acquireLock(), "Should acquire lock");

        // Test 2: Second locker should fail
        System.out.print("Test 2 - Second lock attempt: ");
        DirectoryLocker locker2 = new DirectoryLocker(testDir);
        assertFalse(locker2.acquireLock(), "Should fail to acquire lock");

        // Test 3: Release and reacquire
        System.out.print("Test 3 - Release lock: ");
        locker1.releaseLock();

        // Small delay to ensure lock is released
        Thread.sleep(100);

        System.out.print("Test 4 - Reacquire lock: ");
        assertTrue(locker2.acquireLock(), "Should now acquire lock");

        // Test 5: Static convenience method
        System.out.print("Test 5 - Static lock method: ");
        assertFalse(DirectoryLocker.lockDirectory(testDir),
                "Should fail (already locked)");

        locker2.releaseLock();

        System.out.println("=== All tests passed! ===");
    }

    public static void testLockManager(Path testDir) throws Exception {
        System.out.println("=== Testing LockManager ===");

        // Test with game ID
        System.out.print("Test 1 - Lock game directory: ");
        assertTrue(LockManager.lockGame("test-game", testDir),
                "Should lock game");

        System.out.print("Test 2 - Check if locked: ");
        assertTrue(LockManager.isLocked("game:test-game"),
                "Should show as locked");

        System.out.print("Test 3 - Duplicate lock: ");
        assertFalse(LockManager.lockGame("test-game", testDir),
                "Should fail duplicate lock");

        System.out.print("Test 4 - Unlock: ");
        LockManager.unlockGame("test-game");
        assertFalse(LockManager.isLocked("game:test-game"),
                "Should not be locked after unlock");

        System.out.println("=== LockManager tests passed! ===");
    }

    private static void assertTrue(boolean condition, String message) {
        if (condition) {
            System.out.println("✓ PASS: " + message);
        } else {
            System.out.println("✗ FAIL: " + message);
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    public static void main(String[] args) {
        try {
            Path testDir = Path.of("test_lock_dir");

            // Clean up from previous tests
            try {
                Files.deleteIfExists(testDir.resolve(".lock"));
                Files.deleteIfExists(testDir);
                Files.createDirectories(testDir);
            } catch (IOException e) {
                // Ignore
            }

            testCrossProcessLocking(testDir);
            testLockManager(testDir);

            // Cleanup
            Files.deleteIfExists(testDir.resolve(".lock"));
            Files.deleteIfExists(testDir);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
} // Class