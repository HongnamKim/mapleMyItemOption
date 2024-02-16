package com.example.mapleMyItemOption.domain.character.charaterDataDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterDojang {
    private Integer dojangBestFloor;
}

/*
{
  "date": "2023-12-21T00:00+09:00",
  "character_class": "string",
  "world_name": "string",
  "dojang_best_floor": 0,
  "date_dojang_record": "2023-12-21T00:00+09:00",
  "dojang_best_time": 0
}
 */