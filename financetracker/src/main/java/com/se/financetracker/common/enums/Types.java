package com.se.financetracker.common.enums;

public enum Types {

    MEDICINE("1", "医疗保健"),
    SHOPPING("2", "购物消费"),
    LOAN("3", "贷款还款"),
    FOOD("4", "餐饮消费"),
    ENTERTAINMENT("5", "娱乐消费"),
    EDUCATION("6", "教育知识"),
    OTHER("7", "其他");

    private String type;

    private String typeId;

    private Types(String typeId, String type) {
        this.typeId = typeId;
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public String getTypeId() {
        return typeId;
    }
}
