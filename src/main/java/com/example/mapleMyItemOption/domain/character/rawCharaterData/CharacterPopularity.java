package com.example.mapleMyItemOption.domain.character.rawCharaterData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"date"})
public class CharacterPopularity {
    private Integer popularity;
}
