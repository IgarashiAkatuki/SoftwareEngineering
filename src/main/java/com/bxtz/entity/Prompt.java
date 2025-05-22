package com.bxtz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prompt {

    private String model = "qwen2.5:0.5b";

    private String bills = "";

    private boolean stream = false;

    private String prompt = "";
}
