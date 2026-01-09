/**
 * Author: Stephanos B
 * Date: 17/12/2025
*/

package core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for converting dates between different formats.
 * 
 * @author Stephanos B
 */
public class DateUtil {

    // For directory names: 2024-12-13_19-26-00
    private static final DateTimeFormatter DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // For display in GUI: Dec 13, 2024 7:26 PM
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");

    // For JSON (ISO): 2024-12-13T19:26:00Z (keep this for serialization!)
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    /// /// /// Getters /// /// ///

    /**
     * 
     * @return
     */
    public static String getDirTimestamp() {
        return LocalDateTime.now().format(DIR_FORMATTER);
    }

    public static String getDirDatestamp() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public static String getDisplayTimestamp(LocalDateTime time) {
        return time.format(DISPLAY_FORMATTER);
    }

    public static DateTimeFormatter getJsonFormat() {
        return ISO_FORMATTER;
    }

    /**
     * Result: "20251222_192600" - Sortable, compact, no special chars
     * 
     * @return
     */
    public static String getNumericTimestamp() {
        return String.format("%tY%<tm%<td_%<tH%<tM%<tS", new Date());
    }

    /**
     * Converts a Date object to an ISO 8601 formatted string.
     * 
     * @return
     */
    public static String ISO8601Converter(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String isoDate = sdf.format(date);
        return isoDate;
    }

} // Class
