package com.se.financetracker.common.enums;

public enum Payments {

    ALIPAY("1"),
    WECHAT("2"),
    CASH("3"),
    CREDIT_CARD("4"),
    OTHER("5");

    private String paymentId;

    private Payments(String paymentId) {
       this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

}
