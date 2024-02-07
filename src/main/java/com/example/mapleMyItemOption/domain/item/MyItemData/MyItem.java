package com.example.mapleMyItemOption.domain.item.MyItemData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(value = {"date_expire"})
public class MyItem {
    //private Date dateExpire;
    private String itemEquipmentPart;
    private String itemEquipmentSlot;
    private String itemName;
    private String itemIcon;
    private String itemDescription;
    private String itemShapeName;
    private String itemShapeIcon;
    private String itemGender;

    private MyItemOption itemTotalOption;
    private MyItemOption itemBaseOption;
    private MyItemOption itemAddOption;
    private MyItemOption itemEtcOption;
    private MyItemOption itemExceptionalOption;

    private Integer starforce;
    private String starforceScrollFlag;
    private MyItemOption itemStarforceOption;

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

    private Map<String, Float> totalPotentialValue;
    private Map<String, Float> totalAdditionalvalue;
    private Float totalAddOption;
    private List<Float> totalEtcOption;
}
