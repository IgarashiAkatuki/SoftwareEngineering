package com.bxtz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String prompt = "你是一个智能账单分析助手，请你分析以下账单并给出消费建议:";

    private String bills = "";

    private String msg = "";
}
