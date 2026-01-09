package com.bowen.backend.model;

import lombok.Data;

@Data
public class Phone {
    private String phoneNumber;
    private Type type;

    public enum Type {
        HOME, MOBILE, WORK
    }
}
