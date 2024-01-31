package com.example.mapleMyItemOption.domain.item.rawItemData;

import lombok.Data;

import java.util.Date;

@Data
public class RawItem {

    private Date dateExpire;
    private String itemEquipmentPart;
    private String itemEquipmentSlot;
    private String itemName;
    private String itemIcon;
    private String itemDescription;
    private String itemShapeName;
    private String itemShapeIcon;
    private String itemGender;

    private RawItemOption itemTotalOption;
    private RawItemOption itemBaseOption;
    private RawItemOption itemAddOption;
    private RawItemOption itemEtcOption;
    private RawItemOption itemExceptionalOption;

    private Integer starforce;
    private String starforceScrollFlag;
    private RawItemOption itemStarforceOption;

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
