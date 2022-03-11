package com.reubbishsecurity.CovidVaccinations.authentication.entity.util;

public enum AgeRange {
    AGE_0_10(0, 10, "Age:0-10"), AGE_10_20(10, 20, "Age:10-20"), AGE_20_30(20, 30, "Age:20-30"), AGE_30_40(30, 40, "Age:30-40"), AGE_40_50(40, 50, "Age:40-50"), AGE_50_PLUS(50, -1, "Age:50+");

    public final long lowerAge;
    public final long upperAge;
    public final String text;

    AgeRange(int lowerAge, int upperAge, String text) {
        this.lowerAge = lowerAge;
        this.upperAge = upperAge;
        this.text = text;
    }

    public String getText(){
        return text;
    }
}
