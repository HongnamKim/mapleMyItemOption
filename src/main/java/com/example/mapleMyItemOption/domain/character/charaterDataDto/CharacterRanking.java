package com.example.mapleMyItemOption.domain.character.charaterDataDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
