package com.example.mapleMyItemOption.domain.character.charaterDataDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"date", "artifact_exp", "artifact_point"})
public class CharacterUnion {
    private Integer unionLevel;
    private String unionGrade;
    private Integer artifactLevel;
}

/*
{
  "date": "2024-01-21T00:00+09:00",
  "union_level": 8986,
  "union_grade": "그랜드 마스터 유니온 2",
  "artifact_level": 40,
  "artifact_exp": 11810,
  "artifact_point": 14720
}
 */
