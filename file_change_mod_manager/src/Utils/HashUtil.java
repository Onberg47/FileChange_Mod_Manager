
/*
 * Author Stephanos B
 * Date: 16/12/2025
 */

package utils;

import java.security.MessageDigest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;

/**
 * Used for various Hashing operations.
 * 
 * @author Stephanos B
 */
public class HashUtil {

    /**
     * Supported Hash Algorithms.
     */
    public enum HashAlgorithm {
        SHA256("SHA-256", 64), // 64 hex chars
        MD5("MD5", 32); // Faster but less secure, non-issue for file integrity

        private final String algorithm;
        @SuppressWarnings("unused")
        private final int hexLength; // TODO Redundant, remove?

        HashAlgorithm(String algorithm, int hexLength) {
            this.algorithm = algorithm;
            this.hexLength = hexLength;
        }

    } // HashAlgorithm()

    /// /// /// Methods /// /// ///

    /**
     * Calculates the hash of a file and returns it as a hexadecimal string.
     * (Default: MD5)
     * 
     * @param filePath Path to the file to hash.
     * @return Hexadecimal string of the file's hash.
     * @throws Exception
     */
    public static String computeFileHash(Path filePath) throws Exception {
        return computeFileHash(filePath, HashAlgorithm.MD5);
    } // computeFileHash()

    /**
     * Calculates the hash of a file and returns it as a hexadecimal string.
     * (Configurable)
     * 
     * @author DeepSeek_V3
     * 
     * @param filePath  Path to the file to hash.
     * @param algorithm The hash algorithm to use from the HashAlgorithm enum.
     * @return Hexadecimal string of the file's hash.
     * @throws Exception
     */
    public static String computeFileHash(Path filePath, HashAlgorithm algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm.algorithm);

        try (InputStream is = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[8192]; // 8 KB buffer
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        // Convert byte array to hex string
        return HexFormat.of().formatHex(hashBytes);
    } // computeFileHash()

    public static boolean verifyFileIntegrity(Path filePath, String expectedHash, long expectedSize) {
        try {
            // Check size first (fast)
            long actualSize = Files.size(filePath);
            if (actualSize != expectedSize) {
                return false;
            }

            // Check hash (slower but definitive)
            String actualHash = computeFileHash(filePath); // Your hash calculation method
            return actualHash.equals(expectedHash);

        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            System.err.println("Error Hashing the file! : " + e.getMessage());
            return false;
        }
    } // verifyFileIntegrity()

} // Class