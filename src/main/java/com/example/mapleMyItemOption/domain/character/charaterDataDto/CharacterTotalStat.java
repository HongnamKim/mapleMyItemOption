package com.example.mapleMyItemOption.domain.character.charaterDataDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterTotalStat {
    private Date date;
    private String characterClass;
    private List<FinalStat> finalStat;
    private Integer remainAp;

}


/*
{
  "date": "2024-01-22T00:00+09:00",
  "character_class": "",
  "final_stat": [
    {
      "stat_name": "최소 스탯공격력",
      "stat_value": "43833115"
    },
    {
      "stat_name": "최대 스탯공격력",
      "stat_value": "48703460"
    },
    {
      "stat_name": "데미지",
      "stat_value": "66.00"
    },
    {
      "stat_name": "보스 몬스터 데미지",
      "stat_value": "332.00"
    },
    {
      "stat_name": "최종 데미지",
      "stat_value": "10.00"
    },
    {
      "stat_name": "방어율 무시",
      "stat_value": "94.35"
    },
    {
      "stat_name": "크리티컬 확률",
      "stat_value": "76"
    },
    {
      "stat_name": "크리티컬 데미지",
      "stat_value": "76.10"
    },
    {
      "stat_name": "상태이상 내성",
      "stat_value": "72"
    },
    {
      "stat_name": "스탠스",
      "stat_value": "100"
    },
    {
      "stat_name": "방어력",
      "stat_value": "99999"
    },
    {
      "stat_name": "이동속도",
      "stat_value": "160"
    },
    {
      "stat_name": "점프력",
      "stat_value": "123"
    },
    {
      "stat_name": "스타포스",
      "stat_value": "349"
    },
    {
      "stat_name": "아케인포스",
      "stat_value": "1350"
    },
    {
      "stat_name": "어센틱포스",
      "stat_value": "530"
    },
    {
      "stat_name": "STR",
      "stat_value": "69673"
    },
    {
      "stat_name": "DEX",
      "stat_value": "8201"
    },
    {
      "stat_name": "INT",
      "stat_value": "5408"
    },
    {
      "stat_name": "LUK",
      "stat_value": "5746"
    },
    {
      "stat_name": "HP",
      "stat_value": "69157"
    },
    {
      "stat_name": "MP",
      "stat_value": "20499"
    },
    {
      "stat_name": "AP 배분 STR",
      "stat_value": "1443"
    },
    {
      "stat_name": "AP 배분 DEX",
      "stat_value": "4"
    },
    {
      "stat_name": "AP 배분 INT",
      "stat_value": "4"
    },
    {
      "stat_name": "AP 배분 LUK",
      "stat_value": "4"
    },
    {
      "stat_name": "AP 배분 HP",
      "stat_value": "-449"
    },
    {
      "stat_name": "AP 배분 MP",
      "stat_value": "-197"
    },
    {
      "stat_name": "아이템 드롭률",
      "stat_value": "37"
    },
    {
      "stat_name": "메소 획득량",
      "stat_value": "30"
    },
    {
      "stat_name": "버프 지속시간",
      "stat_value": "32"
    },
    {
      "stat_name": "공격 속도",
      "stat_value": "4"
    },
    {
      "stat_name": "일반 몬스터 데미지",
      "stat_value": "60.00"
    },
    {
      "stat_name": "재사용 대기시간 감소 (초)",
      "stat_value": "0"
    },
    {
      "stat_name": "재사용 대기시간 감소 (%)",
      "stat_value": "5"
    },
    {
      "stat_name": "재사용 대기시간 미적용",
      "stat_value": "0"
    },
    {
      "stat_name": "속성 내성 무시",
      "stat_value": "5.00"
    },
    {
      "stat_name": "상태이상 추가 데미지",
      "stat_value": "0.00"
    },
    {
      "stat_name": "무기 숙련도",
      "stat_value": "90"
    },
    {
      "stat_name": "추가 경험치 획득",
      "stat_value": "97.50"
    },
    {
      "stat_name": "공격력",
      "stat_value": "6938"
    },
    {
      "stat_name": "마력",
      "stat_value": "1801"
    },
    {
      "stat_name": "전투력",
      "stat_value": "169324393"
    },
    {
      "stat_name": "소환수 지속시간 증가",
      "stat_value": "0"
    }
  ],
  "remain_ap": 0
}
 */