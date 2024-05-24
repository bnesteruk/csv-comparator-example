package com.bnesteruk.csv;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSVService {

    public static List<Map<String, String>> s3StreamToObjectList(S3ObjectInputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            /* Convert each CSV record to a map object to have clear <field - value> structure for comparison */
            return csvParser.getRecords().stream()
                    .map(record -> record.toMap())
                    .collect(Collectors.toList());
        }
    }

    /**
     * @param listA list of objects in file A
     * @param listB list of objects in file B
     * @return the list of string representation for each object where at least on field varies.
     */
    public static List<String> compareData(List<Map<String, String>> listA, List<Map<String, String>> listB) {
        List<String> differences = new ArrayList<>();

        /* Check if the given lists are not of the same length.
         * If so - we will need to process the remaining part from the bigger one at the end */
        int minSize = Math.min(listA.size(), listB.size());
        List<Map<String, String>> largerList = listA.size() > listB.size() ? listA : listB;

        /* Iterate over the smaller list */
        for (int i = 0; i < minSize; i++) {
            Map<String, String> mapFromA = listA.get(i);
            Map<String, String> mapFromB = listB.get(i);

            /* Is the current map in list A different from the map in list B? */
            if (!mapFromA.equals(mapFromB)) {
                StringBuilder diffBuilder = new StringBuilder();
                diffBuilder.append("\t{\n");

                /* Check each of the map's fields.
                 * Found difference? Mark the field with < - / + >
                 * No difference ? Just add the field to the string representation with no marks. Output:
                 * {
                 *   - "First Name": "Jane",  <--- from A, show with -
                 *   + "First Name": "Monica" <--- from B, show with +
                 *   - "Last Name": "Smith",
                 *   + "Last Name": "Cameron"
                 *     "Age": "34",           <--- no changes between A and B, just show the field
                 *     "Occupation": "Designer",
                 *     "Company": "Creative Inc.",
                 *   }, */
                for (String key : mapFromA.keySet()) {
                    String valueA = mapFromA.get(key);
                    String valueB = mapFromB.get(key);

                    /* Append the field with < - / + > */
                    if (!valueA.equals(valueB)) {
                        diffBuilder
                                .append("\t\t- \"").append(key).append("\": \"").append(valueA).append("\",\n")
                                .append("\t\t+ \"").append(key).append("\": \"").append(valueB).append("\"\n");
                    } else {
                        /* Append the field with no marks */
                        diffBuilder
                                .append("\t\t  \"").append(key).append("\": \"").append(valueA).append("\",\n");
                    }
                }
                diffBuilder.append("\t},");
                differences.add(diffBuilder.toString());
            }
        }

        /* If the given lists were not of the same length,
         * append the missing objects from the bigger list with the < + > sign */
        List<Map<String, String>> remainingList = largerList.subList(minSize, largerList.size());
        for (int i = 0; i < remainingList.size(); i++) {
            Map<String, String> map = remainingList.get(i);

            StringBuilder diffBuilder = new StringBuilder();
            diffBuilder.append("\t{\n");

            for (String key : map.keySet()) {
                diffBuilder
                        .append("\t\t  + \"").append(key).append("\": \"").append(map.get(key)).append("\",\n");
            }
            diffBuilder.append("\t},");
            differences.add(diffBuilder.toString());
        }
        return differences;
    }
}
