package com.se.financetracker.entity;

import com.se.financetracker.common.payments.Payments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill {

    private int id;

    private String title;

    private String description;

    private long finance;

    private Date date;

    private Payments payments;
}
