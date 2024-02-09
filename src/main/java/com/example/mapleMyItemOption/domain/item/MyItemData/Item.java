package com.example.mapleMyItemOption.domain.item.MyItemData;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Item {
    String itemName;
    String itemImage;
    String itemEquipmentSlot;

    Integer starforce;
    Boolean starforceScroll;

    AddOption addOption;

    List<Float> etcOption; //[공/마, 주스탯]

    Map<String, Float> potentialValue;
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
