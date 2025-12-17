/**
 * Author: Stephanos B
 * Date: 17/12/2025
*/

package Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for converting dates between different formats.
 * 
 */
public class DateConverter {

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
