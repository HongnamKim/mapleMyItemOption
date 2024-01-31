package com.example.mapleMyItemOption.domain.item.rawItemData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RawItemOption {

    private Integer str;
    private Integer dex;
    @JsonProperty("int")
    private Integer intel; // int
    private Integer luk;

    private Integer maxHp;
    private Integer maxMp;

    private Integer attackPower;
    private Integer magicPower;

    private Integer armor;
    private Integer speed;
    private Integer jump;
    private Integer bossDamage;
    private Integer ignoreMonsterArmor;
    private Integer allStat;
    private Integer damage;
    private Integer baseEquipmentLevel;
    private Integer equipmentLevelDecrease;
    private Integer maxMpRate;
    private Integer maxHpRate;

}
