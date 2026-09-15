package com.smartparking.dto;

public class NamedCountDto {
    public String label;
    public long count;

    public NamedCountDto() {}

    public NamedCountDto(String label, long count) {
        this.label = label;
        this.count = count;
    }
}
