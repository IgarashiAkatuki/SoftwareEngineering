package com.se.financetracker.common.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;


@Component
public class CsvUtils {

    public <T> List<T> readCSV(String filename, Class<T> clazz){
        try (FileReader fileReader = new FileReader(filename);
             CSVReader reader = new CSVReader(fileReader)){
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(clazz);

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withMappingStrategy(strategy)
                    .build();

            return csvToBean.parse();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public <T> void writeCSV(String filename, List<T> data){
        try (FileWriter writer = new FileWriter(filename)){

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withOrderedResults(true)
                    .build();
            beanToCsv.write(data);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
