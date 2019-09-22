package com.dragosenic.utilities;

public class UrlParameter {

    private String value;
    private Integer valueAsInteger;

    public UrlParameter(String value) {
        this.value = value;
        this.valueAsInteger = tryParseInt(value);
    }

    public String getValue() {
        return value;
    }

    public boolean isNull() { return value == null; }

    public boolean isInteger() { return valueAsInteger != null; }

    public int asInteger() { return valueAsInteger; }

    private Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
