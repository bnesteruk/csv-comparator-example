package com.bnesteruk.csv;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        /* Read S3 credentials from properties file, check we have all of them specified */
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Unable to find config.properties file in resources.");
                System.exit(1);
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error reading config.properties file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        String accessKey = properties.getProperty("accessKey");
        String secretKey = properties.getProperty("secretKey");
        String regionName = properties.getProperty("region");
        String bucketName = properties.getProperty("bucketName");

        if (accessKey == null
                || secretKey == null
                || regionName == null
                || bucketName == null
        ) {
            System.err.println("Missing required properties in config.properties file.");
            System.exit(1);
        }

        Regions region = Regions.fromName(regionName);

        /* Deal with args passed - check we have all of them specified */
        Map<String, String> arguments = parseArguments(args);

        /* Get the files to read from the cli args */
        String fileAKey = arguments.get("fileAKey");
        String fileBKey = arguments.get("fileBKey");

        /* Uncomment this if you debug the Main class */

        fileAKey = "com.bnesteruk.csv/people_data_A.csv";
        fileBKey = "com.bnesteruk.csv/people_data_B.csv";

        if (fileAKey == null || fileBKey == null) {
            System.err.println("Usage: java -jar your-jar-file.jar fileAKey=<keyA> fileBKey=<keyB>");
            System.exit(1);
        }

        S3Service s3Service = new S3Service(accessKey, secretKey, region);

        List<Map<String, String>> mappedCsvA = null;
        List<Map<String, String>> mappedCsvB = null;

        S3ObjectInputStream inputStream = null;
        try {
            inputStream = s3Service.getS3ObjectInputStream(bucketName, fileAKey);
            mappedCsvA = CSVService.s3StreamToObjectList(inputStream);

            inputStream = s3Service.getS3ObjectInputStream(bucketName, fileBKey);
            mappedCsvB = CSVService.s3StreamToObjectList(inputStream);

        } catch (Exception e) {
            System.err.println("Error converting S3 file to objects list");
            e.printStackTrace();
        } finally {
            s3Service.closeInputStream(inputStream);
        }

        List<String> differences = CSVService.compareData(mappedCsvA, mappedCsvB);

        for (String diff : differences) {
            System.out.println(diff);
        }
    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (String arg : args) {
            String[] keyValue = arg.split("=", 2);
            if (keyValue.length == 2) {
                arguments.put(keyValue[0], keyValue[1]);
            } else {
                System.err.println("Invalid argument: " + arg);
            }
        }
        return arguments;
    }
}
