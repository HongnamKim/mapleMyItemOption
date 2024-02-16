package com.example.mapleMyItemOption.domain.character.charaterDataDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterPopularity {
    private Integer popularity;
}
