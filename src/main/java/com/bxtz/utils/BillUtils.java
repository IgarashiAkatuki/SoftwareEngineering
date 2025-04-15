package com.bxtz.utils;

import com.bxtz.Bill;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BillUtils {

    public static void exportToCSV(List<Bill> bills, File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            StatefulBeanToCsv<Bill> beanToCsv = new StatefulBeanToCsvBuilder<Bill>(writer)
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .build();

            beanToCsv.write(bills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Bill> importFromCSV(File file) {
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) {
            ArrayList<Bill> res = new ArrayList<>();
            for (String[] strings : reader) {
                if ("NAME".equals(strings[0])) {
                    continue;
                }
                Bill bill = new Bill(strings);
                res.add(bill);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

