package com.example.mapleMyItemOption.domain.character.charaterDataDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterAbility {

    List<Ability> abilityInfo;

    @Data
    public static class Ability {
        Integer abilityNo;
        String abilityGrade;
        String abilityValue;
    }
}

/*
{
  "date": "2024-01-21T00:00+09:00",
  "ability_grade": "레전드리",
  "ability_info": [
    {
      "ability_no": "1",
      "ability_grade": "레전드리",
      "ability_value": "아이템 드롭률 20% 증가"
    },
    {
      "ability_no": "2",
      "ability_grade": "유니크",
      "ability_value": "메소 획득량 14% 증가"
    },
    {
      "ability_no": "3",
      "ability_grade": "에픽",
      "ability_value": "일반 몬스터 공격 시 데미지 5% 증가"
    }
  ],
  "remain_fame": 35389,
  "preset_no": 2,
  "ability_preset_1": {
    "ability_preset_grade": "레전드리",
    "ability_info": [
      {
        "ability_no": "1",
        "ability_grade": "레전드리",
        "ability_value": "보스 몬스터 공격 시 데미지 20% 증가"
      },
      {
        "ability_no": "2",
        "ability_grade": "유니크",
        "ability_value": "상태 이상에 걸린 대상 공격 시 데미지 7% 증가"
      },
      {
        "ability_no": "3",
        "ability_grade": "유니크",
        "ability_value": "버프 스킬의 지속 시간 33% 증가"
      }
    ]
  },
  "ability_preset_2": {
    "ability_preset_grade": "레전드리",
    "ability_info": [
      {
        "ability_no": "1",
        "ability_grade": "레전드리",
        "ability_value": "아이템 드롭률 20% 증가"
      },
      {
        "ability_no": "2",
        "ability_grade": "유니크",
        "ability_value": "메소 획득량 14% 증가"
      },
      {
        "ability_no": "3",
        "ability_grade": "에픽",
        "ability_value": "일반 몬스터 공격 시 데미지 5% 증가"
      }
    ]
  },
  "ability_preset_3": {
    "ability_preset_grade": "레전드리",
    "ability_info": [
      {
        "ability_no": "1",
        "ability_grade": "레전드리",
        "ability_value": "메소 획득량 20% 증가"
      },
      {
        "ability_no": "2",
        "ability_grade": "에픽",
        "ability_value": "LUK 18 증가, INT 9 증가"
      },
      {
        "ability_no": "3",
        "ability_grade": "에픽",
        "ability_value": "AP를 직접 투자한 DEX의 5% 만큼 STR 증가"
      }
    ]
  }
}
 */