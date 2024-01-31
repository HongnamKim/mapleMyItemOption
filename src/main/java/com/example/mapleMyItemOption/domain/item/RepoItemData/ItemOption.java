package com.example.mapleMyItemOption.domain.item.RepoItemData;

import lombok.Data;

@Data
public class ItemOption {
    Long itemOptionId; // Primary key
    Long itemId; // Foreign key

    Integer str;
    Integer dex;
    Integer intel; // int
    Integer luk;

    Integer maxHp;
    Integer maxMp;

    Integer attackPower;
    Integer magicPower;

    Integer armor;
    Integer speed;
    Integer jump;
    Integer bossDamage;
    Integer ignoreMonsterArmor;
    Integer allStat;
    Integer damage;
    Integer baseEquipmentLevel;
    Integer equipmentLevelDecrease;
    Integer maxHpRate;
    Integer maxMpRate;
}
