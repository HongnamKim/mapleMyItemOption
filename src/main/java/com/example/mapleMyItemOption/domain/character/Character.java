package com.example.mapleMyItemOption.domain.character;

import com.example.mapleMyItemOption.domain.character.charaterDataDto.CharacterAbility;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

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
    Integer dojangFloor;
    Integer characterPopularity;
    Integer characterUnionLevel;
    List<CharacterAbility.Ability> characterAbility;

    LocalDate date;
    Long assault;

}
