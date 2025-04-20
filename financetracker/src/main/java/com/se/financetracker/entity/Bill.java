package com.se.financetracker.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.se.financetracker.common.enums.Payments;
import com.se.financetracker.common.enums.Types;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill {

    private int id;

    @CsvBindByName(column = "title")
    private String title;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "finance")
    private long finance;

    @CsvBindByName(column = "date")
    @CsvDate("yyyy-MM-dd")
    private Date date;

    @CsvBindByName(column = "payments")
    private Payments payments;

    @CsvBindByName(column = "type")
    private Types type;
}
