package com.example.mapleMyItemOption.domain.character.rawCharaterData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(value = {"character_exp", "character_popularity"})
public class CharacterRanking {
    private Date date;
    private String worldName;
    private Long ranking;
    private String characterName;
    private Integer characterLevel;
    private String className;
    private String subClassName;
    private String characterGuildname;
}
