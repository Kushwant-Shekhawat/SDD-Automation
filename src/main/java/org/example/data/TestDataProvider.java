package org.example.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.utils.ConfigReader;
import org.example.utils.LoggerUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataProvider {

    public static Object[][] getTestData(String csvFileName) {
        List<Object[]> data = new ArrayList<>();
        String path = "testdata/" + csvFileName;

        try (InputStream is = TestDataProvider.class.getClassLoader().getResourceAsStream(path);
             Reader reader = new InputStreamReader(is);
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {

            for (CSVRecord record : parser) {
                Map<String, String> row = new HashMap<>(record.toMap());
                data.add(new Object[]{row});
            }
        } catch (IOException | NullPointerException e) {
            LoggerUtil.error("Failed to load test data from: " + path, e);
        }

        return data.toArray(new Object[0][]);
    }

    public static String getUser(String userType) {
        switch (userType.toLowerCase()) {
            case "locked": return ConfigReader.getConfig("user.locked");
            case "problem": return ConfigReader.getConfig("user.problem");
            case "performance": return ConfigReader.getConfig("user.performance");
            default: return ConfigReader.getConfig("user.standard");
        }
    }

    public static String getPassword() {
        return ConfigReader.getConfig("user.password");
    }
}
