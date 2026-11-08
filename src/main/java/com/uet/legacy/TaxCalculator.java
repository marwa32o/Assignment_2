package com.uet.legacy;

public class TaxCalculator {
    public double calculateTax(String country, double netAmount) {
        double taxRate = 0.15;
        if ("PK".equals(country)) {
            taxRate = 0.17;
        } else if ("US".equals(country)) {
            taxRate = 0.08;
        } else if ("UK".equals(country)) {
            taxRate = 0.20;
        }
        return netAmount * taxRate;
    }
}