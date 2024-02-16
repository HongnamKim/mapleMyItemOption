package com.example.mapleMyItemOption.domain.character.charaterDataDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterUnion {
    private Integer unionLevel;
    private String unionGrade;
    @JsonProperty("union_artifact_level")
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

/*
{
  "date": "2024-02-14T00:00+09:00",
  "union_level": 9000,
  "union_grade": "그랜드 마스터 유니온 2",
  "union_artifact_level": 42,        // 새 key
  "union_artifact_exp": 10410,
  "union_artifact_point": 15400,
  "artifact_level": 42,              // 이전 key 사라질 예정
  "artifact_exp": 10410,
  "artifact_point": 15400
}
 */