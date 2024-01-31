package com.example.mapleMyItemOption.domain.character;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Character {
    private  Long key; // primary key
    String ocid;
    String characterImage;
    String characterName;
    String worldName;
    String characterClass;
    Integer characterLevel;
    String characterGuildName;
    LocalDate date;
    Long assault;

}
