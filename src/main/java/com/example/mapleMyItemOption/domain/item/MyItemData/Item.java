package com.example.mapleMyItemOption.domain.item.MyItemData;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Item {
    String itemName;
    String itemImage;
    String itemEquipmentSlot;
    Integer itemBaseEquipLevel;
    Integer specialRingLevel;

    Integer starforce;
    Boolean starforceScroll;
    Integer compareStarforce;

    AddOption addOption;
    Integer compareAddOption;

    List<Float> etcOption; //[공/마, 주스탯]

    String potentialGrade;
    Map<String, Float> potentialValue;

    String additionalPotentialGrade;
    Map<String, Float> additionalPotentialValue;

    @Data
    public static class AddOption{
        Integer mainStat;
        Integer power;
        Integer bossDamage;
        Integer damage;
        Integer allStat;
    }
}
