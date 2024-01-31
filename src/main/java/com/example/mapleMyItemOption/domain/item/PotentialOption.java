package com.example.mapleMyItemOption.domain.item;

import java.util.ArrayList;
import java.util.List;

public interface PotentialOption {
    String LEGENDARY = "레전드리";
    String UNIQUE = "유니크";
    String EPIC = "에픽";
    String RARE = "레어";

    List<String> GRADE_LIST = new ArrayList<>(List.of(LEGENDARY, UNIQUE, EPIC, RARE));

    String STR = "STR";
    String STR_PERCENT = "STR%";
    String DEX = "DEX";
    String DEX_PERCENT = "DEX%";
    String INT = "INT";
    String INT_PERCENT = "INT%";
    String LUK = "LUK";
    String LUK_PERCENT = "LUK%";
    String HP = "최대 HP";
    String HP_PERCENT = "최대 HP%";
    String ALL_STAT = "올스탯";
    String ALL_STAT_PERCENT = "올스탯%";
    String TOTAL_STAT = "주스탯";
    String TOTAL_STAT_PERCENT = "주스탯%";
    String ATTACK_POWER = "공격력";
    String ATTACK_POWER_PERCENT = "공격력%";
    String MAGIC_POWER = "마력";
    String MAGIC_POWER_PERCENT = "마력%";
    String PER_LEVEL = "캐릭터 기준 9레벨 당";
    String CRITICAL_DAMAGE = "크리티컬 데미지";
    String BOSS_DAMAGE = "보스 몬스터 공격 시 데미지";
    String BOSS_DAMAGE_PERCENT = BOSS_DAMAGE + "%";
    String NORMAL_DAMAGE = "데미지";
    String NORMAL_DAMAGE_PERCENT = NORMAL_DAMAGE + "%";
    String IGNORE_ARMOR = "몬스터 방어율 무시";
    String IGNORE_ARMOR_PERCENT = IGNORE_ARMOR + "%";
    String ITEM_DROP = "아이템 드롭률";
    String ITEM_DROP_PERCENT = ITEM_DROP + "%";
    String MONEY_DROP = "메소 획득량";
    String MONEY_DROP_PERCENT = MONEY_DROP + "%";

    List<String> OPTION_LIST = new ArrayList<>(List.of(ATTACK_POWER, MAGIC_POWER, CRITICAL_DAMAGE, BOSS_DAMAGE, NORMAL_DAMAGE, IGNORE_ARMOR, ITEM_DROP, MONEY_DROP));

    List<String> AVERAGE_LIST = new ArrayList<>(List.of(TOTAL_STAT_PERCENT, PER_LEVEL+" 주스탯", ATTACK_POWER, MAGIC_POWER));
    List<String> TOTAL_LIST = new ArrayList<>(List.of(ATTACK_POWER_PERCENT, MAGIC_POWER_PERCENT, CRITICAL_DAMAGE,
            BOSS_DAMAGE_PERCENT, NORMAL_DAMAGE_PERCENT, IGNORE_ARMOR_PERCENT, ITEM_DROP_PERCENT, MONEY_DROP_PERCENT));
}
