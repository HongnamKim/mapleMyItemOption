package com.example.mapleMyItemOption.exceptions;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IllegalDateException extends IllegalArgumentException{

    private String errorCode;
    private String characterName;
    private String date;

    public IllegalDateException() {
        super();
    }

    public IllegalDateException(String s) {
        super(s);
    }
}
