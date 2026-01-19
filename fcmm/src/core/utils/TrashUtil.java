/**
 * Author Stephanos B
 * Date 17/01/2026
 */
package core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import core.config.AppConfig;

/**
 * @since v3.3.5
 * @author Stephanos B
 */
public class TrashUtil {

    private static final AppConfig config = AppConfig.getInstance();
    private static final Logger log = Logger.getInstance();

    /**
     * Just deletes the entire trash directory then re-makes it.
     */
    public static void emptyTrash() throws Exception {
        log.info(0, "Emptying trash direcotry...");
        FileUtil.deleteDirectory(config.getTrashDir());
        Files.createDirectories(config.getTrashDir());
    }

    /**
     * Deletes all files in trash that are lastModified older than a given date.
     * 
     * @param maxMegabytes Maximum allowed size of the entire Trash directory in
     *                     {@code Megabytes}.
     * @param cutoffDate   Date after which all files older than will be deleted.
     */
    public static void cleanTrash(long maxMegabytes, LocalDate cutoffDate) {
        /// 1. Get a list of files in trash 2. Sort by date
        List<File> fileLs = FileUtil.getAllFiles(config.getTrashDir(), 10)
                .stream()
                .filter(File::isFile)
                .sorted(Comparator.comparingLong(File::lastModified))
                .collect(Collectors.toList());

        long milliseconds = cutoffDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        int total = fileLs.size();

        /// 3. Delete all before the cutoff
        int i = 0, deleteCnt = 0;
        for (File file : fileLs) {
            i++;
            log.info(0, Logger.progressBar(i, total));

            if (file.lastModified() < milliseconds) {
                log.info(1, null, "Deleting old trash: " + file.getPath());
                try {
                    Files.deleteIfExists(fileLs.getFirst().toPath());
                    deleteCnt++;
                    continue;
                } catch (IOException e) {
                    log.error("Could not delete file", e);
                    continue;
                }
            }
            log.info(0, "File is within cut-off date");
            break; // list is order, no need to check after 1 fails.
        }

        /// 4. If the total trash direcoty size is too big:
        float size = megabyte(getDiskSize(config.getTrashDir()));
        if (size > maxMegabytes)
            log.info(1,
                    String.format("Current size of Trash on disk: %.3fMB / %dMB", size, maxMegabytes));

        while (size > maxMegabytes) {
            log.info(1,
                    String.format("size %.3fMB / %dMB", size, maxMegabytes));
            try {
                log.info(1, "Deleting overflow trash: " + fileLs.getFirst());
                Files.deleteIfExists(fileLs.getFirst().toPath());
                deleteCnt++;
            } catch (IOException e) {
                log.error("Could not delete file", e);
                continue;
            }
            size = megabyte(getDiskSize(config.getTrashDir()));
        }

        log.info(0, "Files deleted: " + deleteCnt);
        // TODO clean empty directories
    }

    /// /// /// Utilities /// /// ///

    private static float megabyte(Long bytes) {
        return bytes / (1000000.0f);
    }

    public static long getDiskSize(Path dir) {
        Long size = 0L;
        try {
            size = Files.walk(dir)
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            log.error("Failed to determine trash size", e);
        }
        return size;
    }

    ///

    public static void main(String[] args) {
        TrashUtil.cleanTrash(10, LocalDate.now().minusDays(1));
    }

} // Class