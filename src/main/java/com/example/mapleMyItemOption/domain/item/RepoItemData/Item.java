package com.example.mapleMyItemOption.domain.item.RepoItemData;

import lombok.Data;

import java.util.Date;

@Data
public class Item {
    private Long itemId; // primary key
    private String ocid; // foreign key (character ocid)
    private Long presetId;

    private Date dateExpire;
    private String itemEquipmentPart;
    private String itemEquipmentSlot;
    private String itemName;
    private String itemIcon;
    private String itemDescription;
    private String itemShapeName;
    private String itemShapeIcon;
    private String itemGender;

    /*
    private RawItemOption itemTotalOption;
    private RawItemOption itemBaseOption;
    private RawItemOption itemAddOption;
    private RawItemOption itemEtcOption;
    private RawItemOption itemExceptionalOption;

    private RawItemOption itemStarforceOption;
    */

    private Long itemTotalOptionId;
    private Long itemBaseOptionId;
    private Long itemAddOptionId;
    private Long itemEtcOptionId;
    private Long itemExceptionalOptionId;

    private Integer starforce;
    private Boolean starforceScrollFlag;
    private Long itemStarforceOptionId;

    private String potentialOptionGrade;
    private String potentialOption_1;
    private String potentialOption_2;
    private String potentialOption_3;

    private String additionalPotentialOptionGrade;
    private String additionalPotentialOption_1;
    private String additionalPotentialOption_2;
    private String additionalPotentialOption_3;

    private Integer equipmentLevelIncrease;
    private Long growthExp;
    private Integer growthLevel;

    private Integer scrollUpgrade;
    private Integer scrollUpgradeableCount;
    private String goldenHammerFlag;

    private Integer cuttableCount;
    private Integer scrollResilienceCount;

    private String soulName;
    private String soulOption;

    private Integer specialRingLevel;
}
