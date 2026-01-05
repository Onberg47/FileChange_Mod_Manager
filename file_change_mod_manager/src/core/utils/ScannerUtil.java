/*
 * Author Stephanos B
 * Date: 29/12/2025
 */
package core.utils;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Various utils that use Scanner methods.
 * 
 * @author Stephanos B
 */
public class ScannerUtil {

    /**
     * A utility that uses the System.in stream to scan for user input based on a
     * checklist provided.
     * 
     * @apiNote This does not close the System.in Stream, meant to be used within
     *          other classes with their own Stream
     * 
     * @param checklist A matrix of Keys - Questions - Types.
     * @param close     Whether to close the System.in Stream. If nested within
     *                  another stream you must not close it here!
     * @return A HashMap<String, String> of key-to-question answers.
     * @throws Exception Scanner errors.
     */
    public static HashMap<String, String> checklistConsole(String[][] checklist) throws Exception {
        HashMap<String, String> hMap = new HashMap<>();

        @SuppressWarnings("resource") // THIS IS BY DESIGN!
        // This method is only ever called within the CLI. The CLI has an open System.in
        // Stream and closing it would crash the CLI manager!
        Scanner scanner = new Scanner(System.in);
        try {
            for (int i = 0; i < checklist[0].length; i++) {
                String str = "not defined";
                System.out.print(checklist[1][i] + ": ");
                str = scanner.nextLine().trim();

                if (str.length() > 0)
                    hMap.put(checklist[0][i], str);
            }
            return hMap;
        } catch (Exception e) {
            throw new Exception("Error parsing user data!", e);
        }
    } // checklistConsole()
} // Class
